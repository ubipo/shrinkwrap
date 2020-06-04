find src/main/kotlin -name '*.kt' > FILES
xgettext --keyword=tr --language=java --add-comments --sort-output --omit-header -s -o translations/base.po --files-from=FILES

# ~/scripts/i18n.pl translations/nl.p
