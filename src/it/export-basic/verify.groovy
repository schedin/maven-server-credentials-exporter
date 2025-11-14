File exported = new File(basedir, "target/exported.txt")
if (!exported.isFile()) {
    throw new FileNotFoundException("Expected exported.txt to be created in target directory")
}
String content = exported.text.trim()
if (content != 'alice:sekret') {
    throw new IllegalStateException("Unexpected exported credentials: '${content}'")
}
