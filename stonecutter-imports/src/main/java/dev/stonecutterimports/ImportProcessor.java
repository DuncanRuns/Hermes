package dev.stonecutterimports;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pure text-in / text-out import manager. No Gradle, no I/O.
 *
 * <p>A managed file contains an import section followed by a block comment:
 * <pre>
 * /*!imports
 * com.google.gson.Gson
 * net.minecraft.registry.RegistryOps
 * net.minecraft.util.dynamic.RegistryOps
 * *&#47;
 * </pre>
 *
 * <ul>
 *   <li>{@link #sync}: merges every single-type import in the import section into the block (union, sorted).</li>
 *   <li>{@link #apply}: rewrites the import section so it contains exactly the block entries that are
 *       available in the given {@link ClassIndex} AND whose simple name appears in live (non-comment,
 *       non-string) code. Wildcard and static imports are never removed.</li>
 * </ul>
 */
public final class ImportProcessor {
    private static final Pattern BLOCK = Pattern.compile("/\\*!imports(.*?)\\*/", Pattern.DOTALL);
    private static final Pattern IMPORT_LINE =
            Pattern.compile("^\\s*import\\s+(static\\s+)?([\\w$.]+(?:\\.\\*)?)\\s*;\\s*(?://.*)?$");
    private static final Pattern PACKAGE = Pattern.compile("(?m)^\\s*package\\s+([\\w.]+)\\s*;");

    private final boolean createMissingBlocks;

    /** @param createMissingBlocks if true, {@link #sync} adds a block to files that have imports but no block yet. */
    public ImportProcessor(boolean createMissingBlocks) {
        this.createMissingBlocks = createMissingBlocks;
    }

    /** sync followed by apply. Files without a block are returned unchanged (unless createMissingBlocks). */
    public String process(String text, ClassIndex index) {
        return apply(sync(text), index);
    }

    // ------------------------------------------------------------------ parsing

    private static final class Parsed {
        String nl = "\n";
        String pkg = "";
        int pkgEnd = 0;
        int spanStart = -1, spanEnd = -1; // [start, end) of the import section, end excludes the final line break
        final List<String> singles = new ArrayList<>();
        final List<String> wildcards = new ArrayList<>();
        final List<String> statics = new ArrayList<>();
        boolean hasBlock;
        int blockStart, blockEnd;
        final List<String> blockEntries = new ArrayList<>();
    }

    private static Parsed parse(String text) {
        Parsed p = new Parsed();
        p.nl = text.contains("\r\n") ? "\r\n" : "\n";

        int limit = text.length();
        Matcher bm = BLOCK.matcher(text);
        if (bm.find()) {
            p.hasBlock = true;
            p.blockStart = bm.start();
            p.blockEnd = bm.end();
            limit = p.blockStart;
            for (String line : bm.group(1).split("\\R")) {
                String e = line.trim();
                if (e.startsWith("import ")) e = e.substring(7).trim();
                if (e.endsWith(";")) e = e.substring(0, e.length() - 1).trim();
                if (e.isEmpty() || e.startsWith("//")) continue;
                p.blockEntries.add(e);
            }
        }

        Matcher pm = PACKAGE.matcher(text);
        if (pm.find() && pm.start() < limit) {
            p.pkg = pm.group(1);
            p.pkgEnd = pm.end();
        }

        boolean closed = false;
        int pos = p.pkgEnd;
        while (pos < limit) {
            int eol = text.indexOf('\n', pos);
            int next = eol < 0 ? text.length() : eol + 1;
            int lineEnd = Math.min(eol < 0 ? text.length() : eol, limit);
            if (lineEnd > pos && text.charAt(lineEnd - 1) == '\r') lineEnd--;
            String line = text.substring(pos, Math.max(pos, lineEnd));

            Matcher m = IMPORT_LINE.matcher(line);
            if (m.matches()) {
                if (closed) {
                    throw new IllegalStateException("Import statement at line " + lineNumber(text, pos)
                            + " comes after other content in the import section. Keep all imports together "
                            + "(no comments or Stonecutter conditions between them); conditional imports "
                            + "belong in the /*!imports block instead.");
                }
                if (p.spanStart < 0) p.spanStart = pos;
                p.spanEnd = lineEnd;
                String name = m.group(2);
                if (m.group(1) != null) p.statics.add(name);
                else if (name.endsWith(".*")) p.wildcards.add(name);
                else p.singles.add(name);
            } else if (p.spanStart >= 0 && !line.isBlank()) {
                closed = true;
                if (!p.hasBlock) break;
            }
            pos = next;
        }
        return p;
    }

    private static int lineNumber(String text, int offset) {
        int n = 1;
        for (int i = 0; i < offset && i < text.length(); i++) if (text.charAt(i) == '\n') n++;
        return n;
    }

    // ------------------------------------------------------------------ sync

    public String sync(String text) {
        Parsed p = parse(text);
        if (!p.hasBlock) {
            if (!createMissingBlocks || p.singles.isEmpty()) return text;
            return text.substring(0, p.spanEnd) + p.nl + p.nl
                    + renderBlock(new TreeSet<>(p.singles), p.nl) + text.substring(p.spanEnd);
        }
        TreeSet<String> all = new TreeSet<>(p.blockEntries);
        all.addAll(p.singles);
        return text.substring(0, p.blockStart) + renderBlock(all, p.nl) + text.substring(p.blockEnd);
    }

    private static String renderBlock(Set<String> entries, String nl) {
        StringBuilder sb = new StringBuilder("/*!imports").append(nl);
        for (String e : entries) sb.append(e).append(nl);
        return sb.append("*/").toString();
    }

    // ------------------------------------------------------------------ apply

    public String apply(String text, ClassIndex index) {
        Parsed p = parse(text);
        if (!p.hasBlock) return text;

        int bodyStart = p.spanEnd >= 0 ? p.spanEnd : p.pkgEnd;
        Set<String> used = liveIdentifiers(text, bodyStart);

        Set<String> candidates = new LinkedHashSet<>(p.blockEntries);
        candidates.addAll(p.singles);

        Map<String, List<String>> bySimpleName = new TreeMap<>();
        for (String fqn : candidates) {
            int dot = fqn.lastIndexOf('.');
            if (dot < 0) continue;
            String pkgOf = fqn.substring(0, dot);
            String simple = fqn.substring(dot + 1);
            if (pkgOf.equals(p.pkg)) continue;          // same package: no import needed
            if (!used.contains(simple)) continue;       // not used in live code
            if (!index.contains(fqn)) continue;         // not available in this version
            bySimpleName.computeIfAbsent(simple, k -> new ArrayList<>()).add(fqn);
        }

        TreeSet<String> group1 = new TreeSet<>(), group2 = new TreeSet<>(), group3 = new TreeSet<>();
        for (Map.Entry<String, List<String>> e : bySimpleName.entrySet()) {
            if (e.getValue().size() > 1) {
                throw new IllegalStateException("Ambiguous import for '" + e.getKey() + "': "
                        + e.getValue() + " are all available and used. Remove one from the /*!imports "
                        + "block or use a fully qualified name in code.");
            }
            (isJdk(e.getValue().get(0)) ? group2 : group1).add(e.getValue().get(0));
        }
        for (String w : p.wildcards) (isJdk(w) ? group2 : group1).add(w);
        group3.addAll(p.statics);

        String rendered = renderImports(p.nl, group1, group2, group3);

        if (p.spanStart >= 0) {
            return text.substring(0, p.spanStart) + rendered + text.substring(p.spanEnd);
        }
        if (rendered.isEmpty()) return text;
        int ws = p.blockStart;
        while (ws > p.pkgEnd && Character.isWhitespace(text.charAt(ws - 1))) ws--;
        String lead = ws == 0 ? "" : p.nl + p.nl;
        return text.substring(0, ws) + lead + rendered + p.nl + p.nl + text.substring(p.blockStart);
    }

    private static String renderImports(String nl, Set<String> normal, Set<String> jdk, Set<String> statics) {
        List<String> groups = new ArrayList<>();
        for (String[] spec : new String[][]{{"0"}, {"1"}, {"2"}}) {
            Set<String> g = spec[0].equals("0") ? normal : spec[0].equals("1") ? jdk : statics;
            String prefix = spec[0].equals("2") ? "import static " : "import ";
            if (g.isEmpty()) continue;
            List<String> lines = new ArrayList<>();
            for (String s : g) lines.add(prefix + s + ";");
            groups.add(String.join(nl, lines));
        }
        return String.join(nl + nl, groups);
    }

    private static boolean isJdk(String name) {
        return name.startsWith("java.") || name.startsWith("javax.");
    }

    // ------------------------------------------------------------------ lexer

    /**
     * Returns every identifier in live code from {@code from} onward, skipping comments, string/char literals,
     * text blocks and identifiers directly preceded by '.' (members and fully qualified names).
     */
    public static Set<String> liveIdentifiers(String s, int from) {
        Set<String> out = new java.util.HashSet<>();
        int n = s.length();
        int i = from;
        boolean prevDot = false;
        while (i < n) {
            char c = s.charAt(i);
            if (c == '/' && i + 1 < n && s.charAt(i + 1) == '/') {
                int e = s.indexOf('\n', i);
                i = e < 0 ? n : e;
            } else if (c == '/' && i + 1 < n && s.charAt(i + 1) == '*') {
                int e = s.indexOf("*/", i + 2);
                i = e < 0 ? n : e + 2;
            } else if (s.startsWith("\"\"\"", i)) {
                i += 3;
                while (i < n && !s.startsWith("\"\"\"", i)) i += s.charAt(i) == '\\' ? 2 : 1;
                i = Math.min(n, i + 3);
                prevDot = false;
            } else if (c == '"' || c == '\'') {
                i++;
                while (i < n) {
                    char d = s.charAt(i);
                    if (d == '\\') i += 2;
                    else if (d == c) { i++; break; }
                    else if (d == '\n') break;
                    else i++;
                }
                prevDot = false;
            } else if (Character.isDigit(c)) {
                while (i < n && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_' || s.charAt(i) == '.')) i++;
                prevDot = false;
            } else if (Character.isJavaIdentifierStart(c)) {
                int start = i;
                while (i < n && Character.isJavaIdentifierPart(s.charAt(i))) i++;
                if (!prevDot) out.add(s.substring(start, i));
                prevDot = false;
            } else if (c == '.') {
                prevDot = true;
                i++;
            } else {
                if (!Character.isWhitespace(c)) prevDot = false;
                i++;
            }
        }
        return out;
    }
}
