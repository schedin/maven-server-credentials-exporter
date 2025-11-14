File exported = new File(basedir, "target/classes/exported.txt")
if (!exported.isFile()) {
    throw new FileNotFoundException("Expected exported.txt to be created in target/classes")
}
String content = exported.text.trim()
if (content != 'alice:sekret') {
    throw new IllegalStateException("Unexpected exported credentials: '${content}'")
}
