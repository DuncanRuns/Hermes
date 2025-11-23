package me.duncanruns.hermes.rot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// If this class is copied for external tools using live data, please also include the warning!

/**
 * WARNING: USING CIPHERED DATA DURING THE RUN IS NOT ALLOWED, PLEASE CONTACT THE SPEEDRUN.COM/MC MODERATION TEAM IF YOU WANT TO USE CIPHERED DATA.
 * <br>
 * AVERTISSEMENT : L’UTILISATION DE DONNÉES CHIFFRÉES PENDANT L’EXÉCUTION N’EST PAS AUTORISÉE. VEUILLEZ CONTACTER L’ÉQUIPE DE MODÉRATION DE SPEEDRUN.COM/MC SI VOUS SOUHAITEZ UTILISER DES DONNÉES CHIFFRÉES.
 * <br>
 * ADVERTENCIA: NO SE PERMITE EL USO DE DATOS CIFRADOS DURANTE LA EJECUCIÓN. POR FAVOR, CONTACTE AL EQUIPO DE MODERACIÓN DE SPEEDRUN.COM/MC SI DESEA UTILIZAR DATOS CIFRADOS.
 * <br>
 * WARNUNG: DIE VERWENDUNG VON VERSCHLÜSSELTEN DATEN WÄHREND DES LAUFS IST NICHT ERLAUBT. BITTE KONTAKTIEREN SIE DAS MODERATIONSTEAM VON SPEEDRUN.COM/MC, WENN SIE VERSCHLÜSSELTE DATEN VERWENDEN MÖCHTEN.
 * <br>
 * ПОПЕРЕДЖЕННЯ: ВИКОРИСТАННЯ ЗАШИФРОВАНИХ ДАНИХ ПІД ЧАС СПІДРАНУ НЕ ДОЗВОЛЕНО. БУДЬ ЛАСКА, ЗВ’ЯЖІТЬСЯ З МОДЕРАЦІЙНОЮ КОМАНДОЮ SPEEDRUN.COM/MC, ЯКЩО ВИ ХОЧЕТЕ ВИКОРИСТОВУВАТИ ЗАШИФРОВАНІ ДАНІ.
 * <br>
 * AVVISO: L’USO DI DATI CIFRATI DURANTE L’ESECUZIONE NON È CONSENTITO. SI PREGA DI CONTATTARE IL TEAM DI MODERAZIONE DI SPEEDRUN.COM/MC SE SI DESIDERA UTILIZZARE DATI CIFRATI.
 * <br>
 * AVISO: NÃO É PERMITIDO O USO DE DADOS CIFRADOS DURANTE A EXECUÇÃO. POR FAVOR, ENTRE EM CONTATO COM A EQUIPE DE MODERAÇÃO DE SPEEDRUN.COM/MC SE QUISER UTILIZAR DADOS CIFRADOS.
 * <br>
 * ПРЕДУПРЕЖДЕНИЕ: ИСПОЛЬЗОВАНИЕ ЗАШИФРОВАННЫХ ДАННЫХ ВО ВРЕМЯ ЗАПУСКА ЗАПРЕЩЕНО. ЕСЛИ ВЫ ХОТИТЕ ИСПОЛЬЗОВАТЬ ЗАШИФРОВАННЫЕ ДАННЫЕ, ПОЖАЛУЙСТА, СВЯЖИТЕСЬ С МОДЕРАЦИОННОЙ КОМАНДОЙ SPEEDRUN.COM/MC.
 * <br>
 * 警告：运行过程中不允许使用加密数据，如需使用加密数据，请联系 SPEEDRUN.COM/MC 的管理团队。
 * <br>
 * 警告：実行中に暗号化されたデータを使用することは許可されていません。暗号化データを使用したい場合は、SPEEDRUN.COM/MC のモデレーションチームに連絡してください。
 * <br>
 * 경고: 실행 중 암호화된 데이터를 사용하는 것은 허용되지 않습니다. 암호화된 데이터를 사용하려면 SPEEDRUN.COM/MC의 모더레이션 팀에 문의하십시오.
 */
public class Rotator {
    public static final Rotator ROT_HERMES = new Rotator("!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~", 7499203634667178692L);
    public static final Rotator ROT47 = new Rotator("!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~");

    private final char[] swapArray;
    private char minVal;
    private char maxVal;

    public Rotator(String chars) {
        this(chars.toCharArray());
    }

    public Rotator(char[] chars) {
        this(chars, 0L);
    }

    public Rotator(String chars, long shuffleSeed) {
        this(chars.toCharArray(), shuffleSeed);
    }

    public Rotator(char[] chars, long shuffleSeed) {
        this(chars, shuffleSeed, getShift(chars));
    }

    private static int getShift(char[] chars) {
        if (chars.length % 2 != 0) {
            throw new IllegalArgumentException("Length must be even if shift is not specified.");
        }
        return chars.length / 2;
    }

    public Rotator(char[] chars, long shuffleSeed, int shift) {
        if (shuffleSeed != 0L) shuffle(chars, shuffleSeed);

        maxVal = Character.MIN_VALUE;
        minVal = Character.MAX_VALUE;
        for (char c : chars) {
            maxVal = (char) Math.max(maxVal, c);
            minVal = (char) Math.min(minVal, c);
        }
        swapArray = new char[maxVal - minVal + 1];
        for (int i = 0; i < swapArray.length; i++) {
            swapArray[i] = (char) (i + minVal);
        }
        for (int i = 0; i < chars.length; i++) {
            swapArray[chars[i] - minVal] = chars[(i + shift) % chars.length];
        }
    }

    private static List<Character> toCharacterList(char[] chars) {
        List<Character> list = new ArrayList<>(chars.length);
        for (char c : chars) {
            list.add(c);
        }
        return list;
    }

    private static void shuffle(char[] chars, long shuffleSeed) {
        List<Character> pool = toCharacterList(chars);
        Random random = new Random(shuffleSeed);
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (pool.remove(random.nextInt(pool.size())));
        }
    }

    public String rotate(String input) {
        char[] charArray = input.toCharArray();
        rotate(charArray);
        return new String(charArray);
    }

    public void rotate(char[] charArray) {
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c >= minVal && c <= maxVal) {
                charArray[i] = swapArray[c - minVal];
            }
        }
    }

    private static void halfReverse(char[] chars) {
        for (int i = 0; i < chars.length / 2; i += 2) {
            swap(chars, i, chars.length - 1 - i);
        }
    }

    private static void swap(char[] chars, int i, int i2) {
        char tmp = chars[i];
        chars[i] = chars[i2];
        chars[i2] = tmp;
    }

    public String rotateAndHalfReverse(String input) {
        char[] chars = input.toCharArray();
        rotateAndHalfReverse(chars);
        return new String(chars);
    }

    private void rotateAndHalfReverse(char[] chars) {
        rotate(chars);
        halfReverse(chars);
    }
}

