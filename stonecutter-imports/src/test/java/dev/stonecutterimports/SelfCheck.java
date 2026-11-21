package dev.stonecutterimports;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/** Plain-main checks (no JUnit needed). Run: java -cp out dev.stonecutterimports.SelfCheck [path/to/PlayLog.java] */
public class SelfCheck {
    static int failures = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
        if (!ok) failures++;
    }

    static final String NEW_ACTIVE = String.join("\n",
            "package demo;",
            "",
            "import com.google.gson.Gson;",
            "import net.minecraft.registry.RegistryOps;",
            "import java.util.*;",
            "import static java.lang.Math.max;",
            "",
            "/*!imports",
            "net.minecraft.util.dynamic.RegistryOps",
            "net.minecraft.registry.RegistryOps",
            "net.minecraft.client.gui.screen.Screen",
            "demo.other.Helper",
            "*/",
            "public class A {",
            "    //? if <=1.19.2 {",
            "    /*net.minecraft.util.dynamic.RegistryOps<?> a = RegistryOps.of(null);*/",
            "    //?} else {",
            "    RegistryOps<?> a = RegistryOps.of(null);",
            "    //?}",
            "    Gson g; String s = \"Screen\"; // Screen",
            "    net.minecraft.Foo f; Helper h;",
            "}",
            "");

    // Same file after Stonecutter switched to 1.19.2: branches swapped, imports not yet fixed
    static final String OLD_ACTIVE = String.join("\n",
            "package demo;",
            "",
            "import com.google.gson.Gson;",
            "import net.minecraft.registry.RegistryOps;",
            "import java.util.*;",
            "import static java.lang.Math.max;",
            "",
            "/*!imports",
            "net.minecraft.util.dynamic.RegistryOps",
            "net.minecraft.registry.RegistryOps",
            "net.minecraft.client.gui.screen.Screen",
            "demo.other.Helper",
            "*/",
            "public class A {",
            "    //? if <=1.19.2 {",
            "    RegistryOps<?> a = RegistryOps.of(null);",
            "    //?} else {",
            "    /*RegistryOps<?> a = RegistryOps.of(null);*/",
            "    //?}",
            "    Gson g; String s = \"Screen\"; // Screen",
            "    net.minecraft.Foo f; Helper h;",
            "}",
            "");

    public static void main(String[] args) throws Exception {
        ImportProcessor proc = new ImportProcessor(false);

        ClassIndex newIdx = ClassIndex.ofNames(Arrays.asList(
                "com.google.gson.Gson", "net.minecraft.registry.RegistryOps",
                "net.minecraft.client.gui.screen.Screen", "demo.other.Helper"));
        ClassIndex oldIdx = ClassIndex.ofNames(Arrays.asList(
                "com.google.gson.Gson", "net.minecraft.util.dynamic.RegistryOps",
                "net.minecraft.client.gui.screen.Screen", "demo.other.Helper"));

        // 1. new version
        String r1 = proc.process(NEW_ACTIVE, newIdx);
        check("new: keeps registry.RegistryOps", r1.contains("import net.minecraft.registry.RegistryOps;"));
        check("new: drops dynamic.RegistryOps", !r1.contains("import net.minecraft.util.dynamic.RegistryOps;"));
        check("new: drops Screen (only in string/comment)", !r1.contains("import net.minecraft.client.gui.screen.Screen;"));
        check("new: keeps Helper (used, available)", r1.contains("import demo.other.Helper;"));
        check("new: keeps wildcard + static", r1.contains("import java.util.*;") && r1.contains("import static java.lang.Math.max;"));
        check("new: idempotent", r1.equals(proc.process(r1, newIdx)));

        // 2. old version: now the OTHER RegistryOps must be imported
        String r2 = proc.process(OLD_ACTIVE, oldIdx);
        check("old: imports dynamic.RegistryOps", r2.contains("import net.minecraft.util.dynamic.RegistryOps;"));
        check("old: drops registry.RegistryOps", !r2.contains("import net.minecraft.registry.RegistryOps;"));
        check("old: block still holds both", r2.contains("net.minecraft.registry.RegistryOps\n") && r2.contains("net.minecraft.util.dynamic.RegistryOps\n"));

        // 3. import ordering / grouping
        check("grouping: third-party, blank, java, blank, static",
                r1.contains("import demo.other.Helper;\nimport net.minecraft.registry.RegistryOps;\n\nimport java.util.*;\n\nimport static java.lang.Math.max;"));

        // 4. ambiguity is an error, not a silent pick
        ClassIndex both = ClassIndex.ofNames(Arrays.asList(
                "com.google.gson.Gson", "net.minecraft.util.dynamic.RegistryOps", "net.minecraft.registry.RegistryOps"));
        try {
            proc.process(NEW_ACTIVE, both);
            check("ambiguity throws", false);
        } catch (IllegalStateException e) {
            check("ambiguity throws", e.getMessage().contains("Ambiguous"));
        }

        // 5. CRLF preserved
        String crlf = proc.process(NEW_ACTIVE.replace("\n", "\r\n"), newIdx);
        check("CRLF preserved", crlf.contains("\r\n") && !crlf.replace("\r\n", "").contains("\n"));

        // 6. file with no block is untouched; with createMissingBlocks a block is added
        String plain = "package p;\n\nimport com.google.gson.Gson;\nimport java.util.List;\n\nclass B { Gson g; }\n";
        check("no block: untouched", proc.process(plain, newIdx).equals(plain));
        String created = new ImportProcessor(true).sync(plain);
        check("create blocks: block added with both entries",
                created.contains("/*!imports\ncom.google.gson.Gson\njava.util.List\n*/"));
        check("create blocks: then apply drops unused java.util.List",
                !new ImportProcessor(true).process(plain, newIdx).contains("import java.util.List;"));

        // 7. imports later removed entirely, then needed again: re-inserted before the block
        String noImports = "package p;\n\n/*!imports\ncom.google.gson.Gson\n*/\nclass C { Gson g; }\n";
        String ins = proc.process(noImports, newIdx);
        check("re-insert imports when span is empty", ins.contains("package p;\n\nimport com.google.gson.Gson;\n\n/*!imports"));

        // 8. conditional comment inside the import section is rejected with a useful message
        String bad = "package p;\n\nimport a.B;\n//? if >=1.20\nimport a.C;\n\n/*!imports\n*/\nclass D {}\n";
        try {
            proc.process(bad, newIdx);
            check("comment inside import section rejected", false);
        } catch (IllegalStateException e) {
            check("comment inside import section rejected", e.getMessage().contains("line 5"));
        }

        // 9. lexer edge cases
        String lex = "String a = \"Foo\"; char c = '\"'; String t = \"\"\"\n Bar \"\"\"; /* Baz */ // Qux\n"
                + "x.Quux(); new Real(); 1.0f; Map.Entry<K,V> e;";
        java.util.Set<String> ids = ImportProcessor.liveIdentifiers(lex, 0);
        check("lexer: ignores strings/comments/text blocks/members",
                !ids.contains("Foo") && !ids.contains("Bar") && !ids.contains("Baz") && !ids.contains("Qux") && !ids.contains("Quux"));
        check("lexer: finds real usages", ids.contains("Real") && ids.contains("Map") && !ids.contains("Entry"));

        // 10. real file round trip, if given
        if (args.length > 0) {
            String orig = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
            ImportProcessor p2 = new ImportProcessor(true);
            String synced = p2.sync(orig);
            String origBlock = synced.substring(synced.indexOf("/*!imports"), synced.indexOf("*/", synced.indexOf("/*!imports")) + 2);
            long entries = origBlock.lines().count() - 2;
            // index containing every candidate = "a version where everything exists"
            java.util.List<String> all = new java.util.ArrayList<>();
            origBlock.lines().skip(1).forEach(l -> { if (!l.equals("*/")) all.add(l.trim()); });
            String applied = p2.process(orig, ClassIndex.ofNames(all));
            System.out.println("\nPlayLog: " + entries + " imports collected into block");
            java.util.List<String> before = new java.util.ArrayList<>(), after = new java.util.ArrayList<>();
            orig.lines().filter(l -> l.startsWith("import ")).forEach(before::add);
            applied.lines().filter(l -> l.startsWith("import ")).forEach(after::add);
            java.util.List<String> dropped = new java.util.ArrayList<>(before); dropped.removeAll(after);
            java.util.List<String> added = new java.util.ArrayList<>(after); added.removeAll(before);
            System.out.println("PlayLog imports before=" + before.size() + " after=" + after.size());
            System.out.println("dropped (unused in live code): " + dropped);
            System.out.println("added: " + added);
            check("PlayLog: idempotent", applied.equals(p2.process(applied, ClassIndex.ofNames(all))));
            String origBody = orig.substring(orig.lastIndexOf("\nimport ") + 1);
            origBody = origBody.substring(origBody.indexOf('\n'));
            String newBody = applied.substring(applied.indexOf("*/", applied.indexOf("/*!imports")) + 2);
            check("PlayLog: code below the imports untouched", newBody.equals(origBody.substring(origBody.indexOf("\npublic class"))) || applied.endsWith(origBody.substring(origBody.indexOf("\npublic class"))));
        }

        System.out.println(failures == 0 ? "\nALL PASSED" : "\n" + failures + " FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }
}
