package gq.vapu.czfclient.Util;

import java.util.Random;

public class RamdonUtil {

    public static void main(String[] args) {
        RamdonUtil test = new RamdonUtil();
        System.out.println(test.getStringRandom(8));
    }

    //����������ֺ���ĸ,
    public String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //����length����ʾ���ɼ�λ�����
        for (int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //�����ĸ��������
            if ("char".equalsIgnoreCase(charOrNum)) {
                //����Ǵ�д��ĸ����Сд��ĸ
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }
}