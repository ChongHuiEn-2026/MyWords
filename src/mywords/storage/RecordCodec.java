/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.storage;
import mywords.exception.InvalidInputException;
public class RecordCodec
{
    // Owner: Role A. Step 2: 一行 = 一笔资料,字段用 | 隔开 (INTEGRATION.md §5)
    // 问题: 如果句子里本来就有 | 或换行,会被误当成分隔符 -> 所以要先 "转义" (escape)
    // 规则: \ -> \\    | -> \|    换行 -> \n    \r -> \r    tab -> \t
    public static String escape(String value)
    {
        if (value == null) //null 当作空字串存
        {
            return "";
        }
        StringBuilder sb = new StringBuilder(); //StringBuilder 可以一直 append,比 String + 快
        for (int i = 0; i < value.length(); i++) //一个字一个字看 (one pass,不会重复转义)
        {
            char c = value.charAt(i);
            if (c == '\\') //反斜线本身要先转,不然解码时分不清
            {
                sb.append("\\\\"); //Java 里 "\\\\" 其实是两个反斜线 \\
            }
            else if (c == '|') //分隔符
            {
                sb.append("\\|");
            }
            else if (c == '\n') //换行
            {
                sb.append("\\n");
            }
            else if (c == '\r')
            {
                sb.append("\\r");
            }
            else if (c == '\t')
            {
                sb.append("\\t");
            }
            else
            {
                sb.append(c); //普通字 (包括中文、emoji) 原样放
            }
        }
        return sb.toString();
    }
    public static String join(String[] fields)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++)
        {
            if (i > 0) //第一个字段前面不用 |
            {
                sb.append('|');
            }
            sb.append(escape(fields[i])); //每个字段先转义再接起来
        }
        return sb.toString();
    }
    public static String[] split(String line, int expectedCount) throws InvalidInputException
    {
        // 反过来: 把一行拆回字段。用 "状态" escaping 记住上一个字是不是 \
        String[] fields = new String[expectedCount];
        int count = 0; //已经拆出几个字段
        StringBuilder current = new StringBuilder(); //正在收集的这个字段
        boolean escaping = false; //true = 上一个字是 \,这个字要特别处理
        for (int i = 0; i < line.length(); i++)
        {
            char c = line.charAt(i);
            if (escaping) //上一个是 \ -> 看这个字决定还原成什么
            {
                if (c == '\\')
                {
                    current.append('\\');
                }
                else if (c == '|')
                {
                    current.append('|'); //这是资料里的 |,不是分隔符
                }
                else if (c == 'n')
                {
                    current.append('\n');
                }
                else if (c == 'r')
                {
                    current.append('\r');
                }
                else if (c == 't')
                {
                    current.append('\t');
                }
                else //例如 \x 这种不认识的 -> 档案坏了
                {
                    throw new InvalidInputException("Unknown escape \\" + c);
                }
                escaping = false; //处理完,回到普通状态
            }
            else if (c == '\\') //遇到 \ -> 下一个字要特别处理
            {
                escaping = true;
            }
            else if (c == '|') //没被转义的 | = 真正的分隔符
            {
                if (count >= expectedCount - 1) //字段太多
                {
                    throw new InvalidInputException("Too many fields (expected " + expectedCount + ")");
                }
                fields[count] = current.toString(); //收好这个字段
                count++;
                current.setLength(0); //清空,开始收下一个
            }
            else if (c < 32) //真的控制字元 (没被转义) 不应该出现
            {
                throw new InvalidInputException("Unescaped control character in record");
            }
            else
            {
                current.append(c);
            }
        }
        if (escaping) //最后一个字是 \ 但后面没东西
        {
            throw new InvalidInputException("Record ends with a dangling \\");
        }
        fields[count] = current.toString(); //最后一个字段 (后面没有 |)
        count++;
        if (count != expectedCount) //字段太少
        {
            throw new InvalidInputException("Expected " + expectedCount + " fields but found " + count);
        }
        return fields;
    }
    public static boolean parseBoolean(String text) throws InvalidInputException
    {
        if (text.equals("true")) //只接受完全一样的 true / false
        {
            return true;
        }
        if (text.equals("false"))
        {
            return false;
        }
        throw new InvalidInputException("Expected true or false but found: " + text);
    }
    public static long parseLong(String text) throws InvalidInputException
    {
        try
        {
            return Long.parseLong(text); //"1795478400000" -> 1795478400000L
        }
        catch (NumberFormatException e) //不是数字 -> 转成我们自己的 exception
        {
            throw new InvalidInputException("Expected a number but found: " + text);
        }
    }
}
