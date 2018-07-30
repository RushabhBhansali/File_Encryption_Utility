package edu.gatech.seclass.encode;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static Charset charset = StandardCharsets.UTF_8;


    public static void main(String[] args) throws IOException{
        String filePath;
        boolean c = false;
        int cShift = 0;
        boolean r = false;
        boolean R = false;
        boolean d = false;
        String dCharcters = "";

        if(args == null || args.length == 0) {
            usage(); return;
        }

        filePath = args[args.length-1];
        File resourceFile = new File(filePath);
        if(!resourceFile.exists() ||
                !resourceFile.isFile()) {
            System.err.println("File Not Found"); return;
        }

        //TODO : duplicate code in length = 1 and lenght > 1. REfractor it. - DONE
        if(args.length == 1){
            int charCount = 0;
            Scanner scan = new Scanner(resourceFile);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                charCount = charCount + line.length();
            }
            cShift =  charCount;
            c = true;
            scan.close();

        }

        else{
            for(int i =0;i <= args.length-2;i++){
                if(args[i].equals("-r")){
                     r = true;
                }
                else if(args[i].equals("-R")){
                       R = true;
                }
                else if(args[i].equals("-c")){
                    try{
                            c = true;
                            cShift = Integer.parseInt(args[i+1]);
                            i++;

                    }
                    catch (NumberFormatException e){
                        usage();
                        return;
                    }

                }
                else if(args[i].equals("-d")){
                    if(i< args.length - 2){
                            d = true;
                            dCharcters = args[i+1];
                            i++;
                    }
                    else{
                        usage();
                        return;
                    }
                }
                else{
                    usage();
                    return;
                }
            }
        }

        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(resourceFile.getPath())), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File tempFile = new File("../tempfile.txt");
        FileWriter fw = new FileWriter(tempFile);
        PrintWriter pw = new PrintWriter(fw);

        String newContent = convertLine(content,r,R,c,cShift,d,dCharcters);
        pw.print(newContent);
        pw.flush();
        pw.close();

         resourceFile.delete();
        tempFile.renameTo(resourceFile);
    }

    // for -c
    private static String caeserCipher (String str, int shift){
        if(str.isEmpty()){return str;}
        StringBuilder retStr = new StringBuilder("");
        for(int i = 0;i<=str.length()-1;i++){
            retStr.append(caeserShiftChar(str.charAt(i),shift));
        }

        return retStr.toString();
    }

    //this method will shift the original character as per caeser shift value and return the shifted character
    private static Character caeserShiftChar(Character character, int shift){
        //the code will enter the original character if the char is not an uppercase or lowercase alphabetic character
        if(!(Character.isUpperCase(character) ||
                Character.isLowerCase(character) ||
                Character.isDigit(character))){
            return character;
        }

        if(Character.isDigit(character)){
            int modShift = shift%10;
            int shiftedCharASCII = (int)character + modShift;
            if(shiftedCharASCII> 57){
                shiftedCharASCII = shiftedCharASCII - 10;
            }
            else if(shiftedCharASCII<48){
                shiftedCharASCII = shiftedCharASCII + 10;
            }

            return (char)shiftedCharASCII;
        }

        else{
            int modShift = shift%26;
            int shiftedCharASCII = (int)character + modShift;

            if(Character.isUpperCase(character)){
                if(shiftedCharASCII > 90){
                    shiftedCharASCII = shiftedCharASCII - 26;
                }

                else if(shiftedCharASCII < 65){
                    shiftedCharASCII = shiftedCharASCII + 26;
                }
            }

            else{
                if(shiftedCharASCII > 122){
                    shiftedCharASCII = shiftedCharASCII - 26;
                }

                else if(shiftedCharASCII < 97){
                    shiftedCharASCII = shiftedCharASCII + 26;
                }
            }
            return (char)shiftedCharASCII;
        }
     }

     //for -r
    private static String wordReverse(String str){
        if(str.isEmpty()){return str;}


        char[] charArray = str.toCharArray();
        List<String> listOfreversedWords = new ArrayList<>();
        StringBuilder tempWord = new StringBuilder("");

        //this loop will create the words in reverse character order and save it in the list
        for(int i = charArray.length -1; i>= 0; i--){
            if(!Character.isWhitespace(charArray[i])){
                tempWord.append(charArray[i]);// = tempWord+Character.toString(charArray[i]);
            }

            if(Character.isWhitespace(charArray[i])){
                tempWord.insert(0,charArray[i]);
                listOfreversedWords.add(tempWord.toString());
                tempWord = new StringBuilder("");
                continue;
            }

            if(i==0){
                listOfreversedWords.add(tempWord.toString());
                tempWord = new StringBuilder("");
            }
        }

        StringBuilder retStr = new StringBuilder("");

        //this loop will create string by combining the words in correct order.
        for(int j = listOfreversedWords.size()-1;j>=0;j--){
            retStr.append(listOfreversedWords.get(j));// = retStr + listOfreversedWords.get(j);
        }

        return retStr.toString();
    }

    //for -R flag
    private static String wordOrderReverse(String str){
        StringBuilder tempWord = new StringBuilder();
        //StringBuilder tempWhiteSpace = new StringBuilder();
        Stack wordList = new Stack();

        for(int i=0;i<=str.length()-1;i++) {

            Character ch = str.charAt(i);
            if (!Character.isWhitespace(ch)) {
                tempWord.append(ch);
                if (i == str.length() - 1) {
                    wordList.push(tempWord.toString());
                }
            } else {
                if (tempWord.length() != 0) {
                    wordList.push(tempWord.toString());
                    tempWord = new StringBuilder();
                }
                wordList.push(ch.toString());
            }
        }



        StringBuilder ret = new StringBuilder();

        while(!wordList.isEmpty()){
            ret.append(wordList.pop());
        }

        return ret.toString();
    }

     //for -d
     private static String characterRemove(String inputStr, String deleteCharacters){
         if(deleteCharacters.isEmpty() || inputStr.isEmpty())
         {
             return inputStr;
         }
         int[] asciiArray = new int[128];
         StringBuilder retStr = new StringBuilder("");
         for (Character c : deleteCharacters.toCharArray()) {
             if(Character.isUpperCase(c) || Character.isLowerCase(c)){
                 asciiArray[(int)Character.toUpperCase(c)] = 1;
                 asciiArray[(int)Character.toLowerCase(c)] = 1;
             }
             else{
                 asciiArray[(int)c] = 1;
             }
         }

         for(Character c :inputStr.toCharArray()){
             if(asciiArray[(int)c] != 1){
                 retStr.append(c);
             }
         }
         return retStr.toString();
     }


    //TODO : implement line conversion - DONE
    private static String convertLine(String line,boolean r,boolean R, boolean c, int cShiift, boolean d, String dCharacter){
        String retline = line;
        if(d){
            retline = characterRemove(retline,dCharacter);
        }

        if(r){
            retline = wordReverse(retline);
        }
        if(R){
            retline = wordOrderReverse(retline);
        }

        if(c){
            retline = caeserCipher(retline,cShiift);
        }

        return retline;
    }

    private static void usage() {
        System.err.println("Usage: Encode  [-c int] [-d string] [-r] [-R] <filename>");
        //System.exit(1);
    }



}