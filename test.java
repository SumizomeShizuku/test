import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;

public class CP932DecodeExample {

    // 全角空格 (U+3000)
    private static final String FULLWIDTH_SPACE = "\u3000";

    public static void main(String[] args) {
        // 测试示例：可以换成其他值，比如 "0xFFFF" 试试
        String a = "0x81A1";
        String result = decodeHexWithCP932OrFullwidthSpace(a);
        System.out.println("解码结果：" + result);
    }

    /**
     * 将形如 "0x81A1" / "81A1" / "FAFF" 的十六进制字符串，
     * 先转换为字节序列，再用 CP932 解码。
     * 如果解码失败（非法/未使用区域），返回全角空格 (U+3000)。
     */
    public static String decodeHexWithCP932OrFullwidthSpace(String hexStr) {
        // 1. 去除可选的 "0x"/"0X" 前缀
        if (hexStr.startsWith("0x") || hexStr.startsWith("0X")) {
            hexStr = hexStr.substring(2);
        }

        // 2. 检查长度是否为偶数
        if (hexStr.length() % 2 != 0) {
            // 如果长度不为偶数，说明无法两两配对，也视为非法
            return FULLWIDTH_SPACE;
        }

        // 3. 将十六进制字符串解析为字节数组
        byte[] bytes = new byte[hexStr.length() / 2];
        try {
            for (int i = 0; i < hexStr.length(); i += 2) {
                String byteStr = hexStr.substring(i, i + 2);
                bytes[i / 2] = (byte) Integer.parseInt(byteStr, 16);
            }
        } catch (NumberFormatException e) {
            // 如果出现非十六进制字符，直接返回全角空格
            return FULLWIDTH_SPACE;
        }

        // 4. 用 CP932（windows-31j）解码
        Charset cp932 = Charset.forName("windows-31j");
        CharsetDecoder decoder = cp932.newDecoder()
                                      .onMalformedInput(CodingErrorAction.REPORT)
                                      .onUnmappableCharacter(CodingErrorAction.REPORT);

        try {
            ByteBuffer inBuffer = ByteBuffer.wrap(bytes);
            CharBuffer outBuffer = decoder.decode(inBuffer);
            // 解码成功，返回解码后的字符串
            return outBuffer.toString();
        } catch (CharacterCodingException e) {
            // 解码失败（CP932 未使用区域、非法字节等）
            return FULLWIDTH_SPACE;
        }
    }
}
