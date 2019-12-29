package me.cakegame.database.edit.util;

public class Calculate {
    public static int GridRowPosition(int pos,int rowSize){
        //pos与rowSize都是从0开始
        if (pos == 0)
        {
            return 0;
        }

        return pos - pos % rowSize;
    }

    public static int StringToInt(String str){
        try {
            return Integer.parseInt(str);
        }catch (Exception e)
        {
            return 0;
        }

    }
}
