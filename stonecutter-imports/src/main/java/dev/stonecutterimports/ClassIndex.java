package dev.stonecutterimports;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The set of fully qualified class names available to one Minecraft version.
 * Nested classes are stored with '.' separators (Outer.Inner), matching import syntax.
 * Anything under java.* / javax.* is assumed to exist.
 */
public final class ClassIndex {
    private static final Pattern PACKAGE = Pattern.compile("(?m)^\\s*package\\s+([\\w.]+)\\s*;");
    private final Set<String> names = new HashSet<>();

    public static ClassIndex ofNames(Collection<String> fqns) {
        ClassIndex idx = new ClassIndex();
        idx.names.addAll(fqns);
        return idx;
    }

    public boolean contains(String fqn) {
        return names.contains(fqn) || fqn.startsWith("java.") || fqn.startsWith("javax.");
    }

    public int size() {
        return names.size();
    }

    /** Adds a jar/zip or a directory of .class files. */
    public ClassIndex addClasspathEntry(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            List<Path> files;
            try (Stream<Path> s = Files.walk(path)) {
                files = s.filter(f -> f.toString().endsWith(".class")).collect(Collectors.toList());
            }
            for (Path f : files) addClass(path.relativize(f).toString().replace('\\', '/'));
        } else if (Files.isRegularFile(path)) {
            String n = path.getFileName().toString().toLowerCase();
            if (!n.endsWith(".jar") && !n.endsWith(".zip")) return this;
            try (ZipFile zf = new ZipFile(path.toFile())) {
                Enumeration<? extends ZipEntry> en = zf.entries();
                while (en.hasMoreElements()) addClass(en.nextElement().getName());
            }
        }
        return this;
    }

    /**
     * Adds the project's own top-level classes from .java files. A file only counts if its class name appears
     * in live code, so a file Stonecutter has commented out entirely is not treated as available.
     */
    public ClassIndex addSourceRoot(Path root) throws IOException {
        if (!Files.isDirectory(root)) return this;
        List<Path> files;
        try (Stream<Path> s = Files.walk(root)) {
            files = s.filter(f -> f.toString().endsWith(".java")).collect(Collectors.toList());
        }
        for (Path f : files) {
            String text = new String(Files.readAllBytes(f), StandardCharsets.UTF_8);
            String simple = f.getFileName().toString().replaceAll("\\.java$", "");
            Matcher m = PACKAGE.matcher(text);
            String pkg = m.find() ? m.group(1) : "";
            if (ImportProcessor.liveIdentifiers(text, 0).contains(simple)) {
                names.add(pkg.isEmpty() ? simple : pkg + "." + simple);
            }
        }
        return this;
    }

    private void addClass(String entry) {
        if (!entry.endsWith(".class")) return;
        String s = entry.substring(0, entry.length() - ".class".length());
        if (s.startsWith("META-INF/versions/")) {
            int slash = s.indexOf('/', "META-INF/versions/".length());
            if (slash < 0) return;
            s = s.substring(slash + 1);
        } else if (s.startsWith("META-INF/")) {
            return;
        }
        if (s.equals("module-info") || s.endsWith("/package-info")) return;
        names.add(s.replace('/', '.').replace('$', '.'));
    }
}
