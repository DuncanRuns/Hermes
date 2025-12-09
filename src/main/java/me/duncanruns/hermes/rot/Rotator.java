package me.duncanruns.hermes.rot;

import java.nio.charset.StandardCharsets;
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

    private final byte[] swapArray;
    private char minVal;
    private char maxVal;

    public Rotator(String characters) {
        this(characters.getBytes(StandardCharsets.UTF_8));
    }

    public Rotator(byte[] bytes) {
        this(bytes, 0L);
    }

    public Rotator(String characters, long shuffleSeed) {
        this(characters.getBytes(StandardCharsets.UTF_8), shuffleSeed);
    }

    public Rotator(byte[] bytes, long shuffleSeed) {
        this(bytes, shuffleSeed, getShift(bytes));
    }

    public Rotator(byte[] characters, long shuffleSeed, int shift) {
        if (shuffleSeed != 0L) shuffle(characters, shuffleSeed);

        maxVal = Character.MIN_VALUE;
        minVal = Character.MAX_VALUE;
        for (byte c : characters) {
            maxVal = (char) Math.max(maxVal, c);
            minVal = (char) Math.min(minVal, c);
        }
        swapArray = new byte[maxVal - minVal + 1];
        for (int i = 0; i < swapArray.length; i++) {
            swapArray[i] = (byte) (i + minVal);
        }
        for (int i = 0; i < characters.length; i++) {
            swapArray[characters[i] - minVal] = characters[(i + shift) % characters.length];
        }
    }

    private static int getShift(byte[] characters) {
        if (characters.length % 2 != 0) {
            throw new IllegalArgumentException("Length must be even if shift is not specified.");
        }
        return characters.length / 2;
    }

    private static List<Byte> toByteList(byte[] bytes) {
        List<Byte> list = new ArrayList<>(bytes.length);
        for (byte c : bytes) {
            list.add(c);
        }
        return list;
    }

    private static void shuffle(byte[] bytes, long shuffleSeed) {
        List<Byte> pool = toByteList(bytes);
        Random random = new Random(shuffleSeed);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (pool.remove(random.nextInt(pool.size())));
        }
    }

    private static void halfReverse(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; i += 2) {
            swap(bytes, i, bytes.length - 1 - i);
        }
    }

    private static void swap(byte[] bytes, int i, int i2) {
        byte tmp = bytes[i];
        bytes[i] = bytes[i2];
        bytes[i2] = tmp;
    }

    public void rotate(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            byte c = bytes[i];
            if (c >= minVal && c <= maxVal) {
                bytes[i] = swapArray[c - minVal];
            }
        }
    }

    public void rotateAndHalfReverse(byte[] bytes) {
        rotate(bytes);
        halfReverse(bytes);
    }
}

