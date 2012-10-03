package ndnrp.util;


import java.util.*;
import java.util.regex.Pattern;

public class StrValidator{
    public static final Character INVALID_CHAR [] = {'\"',  '{',  '}',
                                            ',',  '[',  ']',
                                            ':',  '!',  '&',
                                            '$',  '(',  ')',
                                            ';',  '=',  '+',
                                            '*',  '@',  ' ',
                                           '\''};

    public static final String HEAD_MARK = ".~";
    public static final String TAIL_MARK = "~.";

    private Hashtable<Character, String> _inChar2str = null;
    private Hashtable<String, Character> _str2inChar = null;

    public StrValidator(){
        _inChar2str = new Hashtable<Character, String>();
        _str2inChar = new Hashtable<String, Character>();

        Character inChar = null;
        String valid = null;
        for(int i = 0 ; i < INVALID_CHAR.length; ++i){
            inChar = INVALID_CHAR[i];
            valid = HEAD_MARK + i + TAIL_MARK;
            _inChar2str.put(inChar, valid);
            _str2inChar.put(valid, inChar);
        }
    }

    public String toValid(String str){
        Iterator<Character> iter = _inChar2str.keySet().iterator();
        Character oriChar = null;
        while(iter.hasNext()){
            oriChar = iter.next();
            String ori = oriChar.toString();
            String res = _inChar2str.get(oriChar);
            str = str.replaceAll(Pattern.quote(ori), res);
        }
        return str;
    }

    public String fromValid(String str){
        Iterator<String> iter = _str2inChar.keySet().iterator();
        while(iter.hasNext()){
            String ori = iter.next();
            String res = _str2inChar.get(ori).toString();
            str = str.replaceAll(Pattern.quote(ori), res);
        }
        return str;
    }

}

