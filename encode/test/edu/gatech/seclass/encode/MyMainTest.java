package edu.gatech.seclass.encode;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyMainTest {

    private Charset charset = StandardCharsets.UTF_8;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    //creates temporary file with provided String
    private File createFile(String str) throws IOException{
        File file = temporaryFolder.newFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(str);
        fileWriter.close();
        return file;
    }

    private File createFileNotClosed(String str) throws IOException{
        File file = temporaryFolder.newFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(str);
       // fileWriter.close();
        return file;
    }

    private String getFileContent(String filename) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    //generic error message as defined in usage() of the Main method
    private static String errorMessage = "Usage: Encode  [-c int] [-d string] [-r] [-R] <filename>\r\n";
    private static String errMessageFileNotFound = "File Not Found\r\n";
	
/*
Place all  of your tests in this class, optionally using MainTest.java as an example.
*/
    //purpose: The Program should generate the error if more than 3 OPT flags are provided
    // Frame #: 1
    @Test
    public void encodeTest1() throws Exception{
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        File inputFile = createFile("abcd");
        String[] args ={"-d","ab","-c","2","-r","-c","2",inputFile.getPath()} ;
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        String expected = "fe";
//        byte[] errMsgByte = (errorMessage).getBytes();
//        byte[] outbyte = outContent.toByteArray();
        String outString = outContent.toString();
        //assertTrue(errorMessage.equals(outString));
        assertEquals("",outString);
        assertEquals(expected,actualOutput);
//        Assert.assertArrayEquals(errMsgByte,outbyte);
       // assertEquals("Usage: Encode  [-c int] [-d string] [-r] <filename>\n",outContent.toString());
    }

    //purpose: The program should generate error if the first OPT flag is neither of -c,-r or -d
    // Frame # : 2
    @Test
    public void encodeTest2() throws Exception{
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        File inputFile = createFile("abcd");
        String[] args ={"-b","ab","-r","-c","2",inputFile.getPath()} ;
        Main.main(args);
        assertEquals(errorMessage,outContent.toString());
    }

    //purpose: The program should generate error if the second OPT flag is neither of -c,-r or -d
    // Frame # : 3
    @Test
    public void encodeTest3() throws Exception{
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        File inputFile = createFile("abcd");
        String[] args ={"-d","ab","-g","-c","2",inputFile.getPath()} ;
        Main.main(args);
        assertEquals(errorMessage,outContent.toString());
    }

    //purpose: The program should generate error if the third OPT flag is neither of -c,-r or -d
    // Frame # : 4
    @Test
    public void encodeTest4() throws Exception{
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        File inputFile = createFile("abcd");
        String[] args ={"-d","ab","-r","-g","2",inputFile.getPath()} ;
        Main.main(args);
        assertEquals(errorMessage,outContent.toString());
    }

    //purpose: The program should generate error if the <integer> for -c flag is not an integer
    //Frame #: 5
    @Test
    public void encodeTest5() throws Exception{
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        File inputFile = createFile("abcd");
        String[] args ={"-d","ab","-r","-c","ab",inputFile.getPath()} ;
        Main.main(args);
        assertEquals(errorMessage,outContent.toString());
    }

    //purpose: The program should generate error if the <character> for -d flag is not defined (empty)
    //Frame #: 6
    @Test
    public void encodeTest6() throws Exception{
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        File inputFile = createFile("abcd");
        String[] args ={"-r","-c","2","-d",inputFile.getPath()} ;
        Main.main(args);
        assertEquals(errorMessage,outContent.toString());
    }

    //purpose: The program should generate error if the file corresponding to <filename> is not present
    //Frame #: 7
    @Test
    public void encodeTest7() throws Exception{
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        String[] args ={"-r","-c","2","-d","ab","dummyFileName"} ;
        Main.main(args);
        assertEquals(errMessageFileNotFound,outContent.toString());
    }

    //purpose: Check the accuracy for ceaser cipher algorithm
    //Frame #: 15
    @Test
    public void encodeTest8() throws Exception{
        //test file content
        String fileContent = "abcdefghijklmnopqrstuvwxyz123@1234567890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","8",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());

        //expected output content
        String expectedOutput = "ijklmnopqrstuvwxyzabcdefgh901@9012345678!@#$%^&*(";

        assertEquals("caeser encryption successful",expectedOutput,actualOutput);
    }

    //purpose: Check the accuracy character elimination algorithm (-d)
    //Frame #: 21
    @Test
    public void encodeTest9() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-d","ghgijklm1@",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());

        //expected output content
        String expectedOutput = "abcdef  nopqrs tuvwxyz  23 23456 7890!#$%^&*(";

        assertEquals("character elimination successful",expectedOutput,actualOutput);
    }

    //purpose: Check the accuracy of word reversal algorithm (-r)
    //Frame #: 18
    @Test
    public void encodeTest10() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890 !@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-r",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());

        //expected output content
        String expectedOutput = "fedcba mlkjihg srqpon zyxwvut mlkjihg 1@321 65432 0987 (*&^%$#@!";
        assertEquals("word reversal successful",expectedOutput,actualOutput);
    }

    //purpose: Verify -c and -d together. Order: -c <integer> -d <character>
    //Frame #: 25
    @Test
    public void encodeTest11() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890 !@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-d","ghijklm1@","-c","8",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "ijklmn  vwxyza bcdefgh  01 01234 5678 !#$%^&*(";
        assertEquals("character elimination and cesaer encryption successful",expectedOutput,actualOutput);
    }

    //purpose: Verify -c and -r together
    //Frame #: 26
    @Test
    public void encodeTest12() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890 !@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-r","-c","31",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "kjihgf rqponml xwvuts edcbazy rqponml 2@432 76543 1098 (*&^%$#@!";
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
    }

    //purpose: Verify -d and -r together
    //Frame #: 30
    @Test
    public void encodeTest13() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-d","ghijklm1@","-r",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "fedcba  srqpon zyxwvut  32 65432 (*&^%$#!0987";
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
    }

    //purpose: Verify -c, -r and -d together
    // Frame #:37
    @Test
    public void encodeTest14() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","18","-r","-d","ghijklm1@",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "xwvuts  kjihgf rqponml  10 43210 (*&^%$#!8765";
        assertEquals("character elimination, word reversal and cesaer encryption successful",expectedOutput,actualOutput);
    }

    //purpose: If the input file is empty, an empty output file should be generated.
    //Frame: #:8
    @Test
    public void encodeTest15() throws Exception{
        //test file content
        String fileContent = "";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","18","-r","-d","ghijklm1@",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "";
        assertEquals("empty file successfully verified",expectedOutput,actualOutput);
    }


    //purpose: program should generate error message if -r flag repeated twice
    //Frame#: 42
    @Test
    public void encodeTest16() throws Exception{
      /*  ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));*/
        File inputFile = createFile("abCD");
        String[] args ={"-r","-c","3","-r",inputFile.getPath()} ;
        String expected = "GFed";
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        assertEquals(expected,actualOutput);
    }

    //purpose: program should generate error message if -d flag is repeated
    //Frame#: 49
    @Test
    public void encodeTest17() throws Exception{
        /*ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));*/
        File inputFile = createFile("abcd");
        String[] args ={"-r","-d","a","-d","cf",inputFile.getPath()} ;
        Main.main(args);
        String expected = "dba";
        String actualOutput = getFileContent(inputFile.getPath());
        assertEquals(expected,actualOutput);
    }

    //purpose: program should generate error message if -c flag is repeated
    //Frame#: 50
    @Test
    public void encodeTest18() throws Exception{
       /* ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));*/
        File inputFile = createFile("abcd");
        String[] args ={"-c","2","-c","5",inputFile.getPath()} ;
        Main.main(args);
        String expected = "fghi";
        String actualOutput = getFileContent(inputFile.getPath());
        assertEquals(expected,actualOutput);
    }

    //purpose: testing outcome of the file when no OPT flag is provided
    //Frame #: 12
    @Test
    public void encodeTest19() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "lmnopq rstuvwx yzabcd efghijk rstuvwx 456@4 56789 0123!@#$%^&*(";
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
    }

    //purpose: Testing outcome of the program when "-r" is provided as <characters> for -d flag, and -r flag is also provided for word reversal
    //Frame #: 28
    @Test
    public void encodeTest20() throws Exception{
        //test file content
        String fileContent = "abchd -rch kj-ghr";
        File inputFile = createFile(fileContent);
        String[] args = {"-r","-d","-r",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "dhcba hc hgjk";
        //"abchd ch kjgh"
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
    }

    //purpose: Testing outcome of the program with empty file and valid  flag
    //Frame #: 8
    @Test
    public void encodeTest21() throws Exception{
        //test file content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        String fileContent = "";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","2",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "";
        //"abchd ch kjgh"
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
        assertEquals("",outContent.toString());
    }

    //purpose: Testing outcome of the program with empty file and invalid flag
    //Frame #: 8
    @Test
    public void encodeTest22() throws Exception{
        //test file content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        String fileContent = "";
        File inputFile = createFile(fileContent);
        String[] args = {"-h","2",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "";
        //"abchd ch kjgh"
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
        assertEquals(errorMessage,outContent.toString());
    }

    //purpose: Testing outcome of the program with empty file and valid flag
    //Frame #: 8
    @Test
    public void encodeTest23() throws Exception{
        //test file content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        String fileContent = "";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","2",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "";
        //"abchd ch kjgh"
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
        assertEquals("",outContent.toString());
    }

    //purpose: Check the accuracy for ceaser cipher algorithm with empty <integer> value
    //Frame #: 13
    @Test
    public void encodeTes24() throws Exception{
        //test file content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        String fileContent = "abcdefghijklmnopqrstuvwxyz123@1234567890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "abcdefghijklmnopqrstuvwxyz123@1234567890!@#$%^&*(";
        //"abchd ch kjgh"
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
        assertEquals(errorMessage,outContent.toString());
    }

    //purpose: Check the accuracy for ceaser cipher algorithm with zero <integer> value
    //Frame #: 14
    @Test
    public void encodeTes25() throws Exception{
        //test file content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        String fileContent = "ABCDefghijklmnopqrstuvwxyz123@1234567890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","0",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "ABCDefghijklmnopqrstuvwxyz123@1234567890!@#$%^&*(";
        //"abchd ch kjgh"
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
        assertEquals("",outContent.toString());
    }

    //purpose: Check the accuracy for ceaser cipher algorithm with "26" <integer> value
    //Frame #: 16
    @Test
    public void encodeTes26() throws Exception{
        //test file content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        String fileContent = "abcdefghijklmnopqrstuvwxyz123@1234567890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","26",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "abcdefghijklmnopqrstuvwxyz789@7890123456!@#$%^&*(";
        //"abchd ch kjgh"
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
        assertEquals("",outContent.toString());
    }

    //purpose: Check the accuracy for ceaser cipher algorithm with  <integer> value greater than 26
    //Frame #: 16
    @Test
    public void encodeTes27() throws Exception{
        //test file content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        String fileContent = "abcdefghijklmnopqrstuvwxyz123@1234567890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","30",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "efghijklmnopqrstuvwxyzabcd123@1234567890!@#$%^&*(";
        //"abchd ch kjgh"
        assertEquals("word reversal and cesaer encryption successful",expectedOutput,actualOutput);
        assertEquals("",outContent.toString());
    }

    //purpose: program should generate error message if -r flag repeated three times
    //Frame#: 45
    @Test
    public void encodeTest28() throws Exception{
        /*ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));*/
        File inputFile = createFile("abcd");
        String[] args ={"-r","-r","-r",inputFile.getPath()} ;
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        assertEquals("dcba",actualOutput);
    }
    //purpose: Verify outcome with flags -d, -r, -c
    // Frame #:53
    @Test
    public void encodeTest29() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890!@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-d","ghijklm1@","-r","-c","18",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "xwvuts  kjihgf rqponml  10 43210 (*&^%$#!8765";
        assertEquals("character elimination, word reversal and cesaer encryption successful",expectedOutput,actualOutput);
    }

    //purpose: Verify -c and -d together. Order: -d <character> -c <integer>
    //Frame #: 26
    @Test
    public void encodeTest30() throws Exception{
        //test file content
        String fileContent = "abcdef ghijklm nopqrs tuvwxyz ghijklm 123@1 23456 7890 !@#$%^&*(";
        File inputFile = createFile(fileContent);
        String[] args = {"-c","8","-d","ghijklm1@",inputFile.getPath()};
        Main.main(args);
        String actualOutput = getFileContent(inputFile.getPath());
        //expected output content
        String expectedOutput = "ijklmn  vwxyza bcdefgh  01 01234 5678 !#$%^&*(";
        assertEquals("character elimination and cesaer encryption successful",expectedOutput,actualOutput);
    }


}
