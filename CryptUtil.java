import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Date;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

public class CryptUtil {
	
//////////////////////////////////////////////////////////
// BCrypt
//////////////////////////////////////////////////////////

	/**
	 * BCyptでハッシュ化(strength指定なし、デフォルトでは10)
	 * @param passStr パスワード文字列
	 */
	public static String BCryptEncode(String passStr) {
		
		// BCryptでのハッシュ化
		BCryptPasswordEncoder bpe = new BCryptPasswordEncoder();
		String hashStr = bpe.encode(passStr);
		
	    return hashStr;
	}

	/**
	 * BCyptでハッシュ化
	 * @param passStr パスワード文字列
	 * @param strength ハッシュ化の回数。4～31（2の何乗か）
	 */
	public static String BCryptEncode(String passStr, int strength) {
		
		// BCryptでのハッシュ化
		BCryptPasswordEncoder bpe = new BCryptPasswordEncoder(strength);
		String hashStr = bpe.encode(passStr);
		
	    return hashStr;
	}
	
	/**
	 * BCｒｙｐｔで生パスワードとハッシュが合致するかの認証(strength指定なし、デフォルトでは10
	 * @param passStr 生
	 * @param hashStr ハッシュ
	 */
	public static boolean BCryptVerify(String passStr, String hashStr) {
		
		// パスワードがハッシュに一致するかのチェック
		BCryptPasswordEncoder bpe = new BCryptPasswordEncoder();
		boolean isMatch = bpe.matches(passStr, hashStr);
		
		return isMatch;
	}

	/**
	 * BCｒｙｐｔで生パスワードとハッシュが合致するかの認証
	 * @param passStr 生
	 * @param hashStr ハッシュ
	 * @param strength 強さ（ストレッチ回数、2のn乗回）
	 */
	public static boolean BCryptVerify(String passStr, String hashStr, int strength) {
		
		// パスワードがハッシュに一致するかのチェック
		BCryptPasswordEncoder bpe = new BCryptPasswordEncoder(strength);
		boolean isMatch = bpe.matches(passStr, hashStr);
		
		return isMatch;
	}
	
	
	/**
	 * BCryptの検証を実行
	 */
	private static void testBCrypt() {
		
		String passStr = "password_desu_4";
		String hashStr;
		boolean isMatch = false;
		
		// エンコード
		hashStr = BCryptEncode(passStr);
		System.out.println("original string   : " + passStr);
		System.out.println("encoded by Bcrypt : " + hashStr);
		
		// 認証（OKパターン）
		isMatch = BCryptVerify(passStr, hashStr);
		System.out.println("verifying string  : " + passStr);
		System.out.println("result            : " + (isMatch ? "matche" : "not matche"));		
		
		// 認証（NGパターン）
		String wrongPass = "koreha_chigau_4";
		isMatch = BCryptVerify(wrongPass, hashStr);
		System.out.println("verifying string  : " + wrongPass);
		System.out.println("result            : " + (isMatch ? "matche" : "not matche"));		
	}

	/**
	 * BCryptの処理時間計測
	 */
	private static void measureTimeBCrypt() {

		String passStr = "password_desu_4";
		double streachTimes; 

		// 4から31だけど31までやると一週間くらいかかりそう
		// for (int strength=4; strength<=31; strength++) {
		for (int strength=4; strength<=20; strength++) {
			streachTimes = Math.pow(2, strength);
			System.out.println("= strength : " + strength + ", streach times : " + String.format("%1$.0f", streachTimes) + " ========");

			// 開始時刻
			long startTime = (new Date()).getTime();

			BCryptEncode(passStr, strength);
			
			// ハッシュ化終了時刻
			long endTime = (new Date()).getTime();
			String timeDelta = String.format("%.3f", ((float)(endTime - startTime) / 1000));
			
		    System.out.println("encoding time  : " + timeDelta + " sec.");
		    System.out.println("");

		}		
		
	}
	
//////////////////////////////////////////////////////////
// PBKDF2
//////////////////////////////////////////////////////////

	// アルゴリズムの指定（https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory）
	// PBKDF2なら、PBKDF2With<擬似乱数関数>という書式で指定できるらしいが、関数にHmacSHA256以外に何が指定できるのかは不明
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	
	/**
	 * PBKDF2用のsalt生成（SHA-256）
	 * 
	 * @param orgStr
	 * @return
	 */
	public static byte[] getSHA256salt() {
		byte[] hash = null;
		
		String salt = UUID.randomUUID().toString().replaceAll("-", "");

	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        hash = md.digest(salt.getBytes());

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }

	    return hash;
	}
	
	/**
	 * PBKDF2でハッシュ化
	 * @param passStr パスワード文字列
	 * @param salt
	 * @param iterateCount ストレッチ回数
	 * @param keyLength
	 */
	public static String PBKDF2Encode(String passStr, byte[] salt, int iterateCount, int keyLengh) {

		String hashStr = null;

		char[] passCharAry = passStr.toCharArray();

		try {
			// 鍵の仕様を指定
			PBEKeySpec keySpec = new PBEKeySpec(passCharAry, salt, iterateCount, keyLengh);
			// ハッシュ化
			SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey sk = skf.generateSecret(keySpec);
			byte[] hashByteAry = sk.getEncoded();

			hashStr = DatatypeConverter.printHexBinary(hashByteAry);

		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return hashStr;
	}
	
	/**
	 * PBKDF2で生パスワードとハッシュが合致するかの認証
	 * @param passStr 生
	 * @param hashStr ハッシュ
	 * @param strength 強さ（ストレッチ回数、2のn乗回）
	 */
	public static boolean PBKDF2Verify(String passStr, String hashedStr, byte[] salt, int iterateCount, int keyLengh) {
		
		boolean isMatch = false;
		String newHashStr;

		// パスワードを再度ハッシュ化、さっきのハッシュに一致するかのチェック		
		char[] passCharAry = passStr.toCharArray();
		
		try {
			// 鍵の仕様を指定
			PBEKeySpec keySpec = new PBEKeySpec(passCharAry, salt, iterateCount, keyLengh);
			// ハッシュ化
			SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey sk = skf.generateSecret(keySpec);
			byte[] hashByteAry = sk.getEncoded();

			newHashStr = DatatypeConverter.printHexBinary(hashByteAry);
			
			if (newHashStr.equals(hashedStr)) { 
				isMatch = true;
			}

		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return isMatch;
	}

	/**
	 * PBKDF2の検証を実行
	 */
	private static void testPBKDF2() {

		String passStr = "password_desu_4";
		int iterateCount = 1024;
		int keyLengh = 256;
		String hashStr;
		boolean isMatch = false;
		
		// エンコード
		byte[] salt = getSHA256salt();
		hashStr = PBKDF2Encode(passStr, salt, iterateCount, keyLengh);
		System.out.println("original string   : " + passStr);
		System.out.println("encoded by Bcrypt : " + hashStr);
		
		// 認証（OKパターン）
		isMatch = PBKDF2Verify(passStr, hashStr, salt, iterateCount, keyLengh);
		System.out.println("verifying string  : " + passStr);
		System.out.println("result            : " + (isMatch ? "matche" : "not matche"));		
		
		// 認証（NGパターン）
		String wrongPass = "koreha_chigau_4";
		isMatch = PBKDF2Verify(wrongPass, hashStr, salt, iterateCount, keyLengh);
		System.out.println("verifying string  : " + wrongPass);
		System.out.println("result            : " + (isMatch ? "matche" : "not matche"));		

	}
	
	/**
	 * PBKDF2の処理時間計測
	 */
	private static void measureTimePBKDF2() {

		String passStr = "password_desu_4";
		int keyLength = 256;
		byte[] salt = getSHA256salt();

		// 範囲は暫定で2の4乗から20乗まで（BCryptに合わせた）
		for (int strength=4; strength<=20; strength++) {
			
			int iterateCount = (int) Math.pow(2, strength);
			System.out.println("= iterateCount : " + iterateCount + " ========");

			// 開始時刻
			long startTime = (new Date()).getTime();

			PBKDF2Encode(passStr, salt, iterateCount, keyLength);
			
			// ハッシュ化終了時刻
			long endTime = (new Date()).getTime();
			String timeDelta = String.format("%.3f", ((float)(endTime - startTime) / 1000));
			
		    System.out.println("encoding time  : " + timeDelta + " sec.");
		    System.out.println("");
		}		
	}
	
//////////////////////////////////////////////////////////
// main
//////////////////////////////////////////////////////////
	
	public static void main(String[] args) {

		// 全部やると長くなるので適宜コメントアウトすること
		
		// BCryptの確認
		testBCrypt();
		// BCryptの時間計測
		measureTimeBCrypt();
		
		// PBKDF2の確認
		testPBKDF2();
		// PBKDF2の時間計測
		measureTimePBKDF2();

	}

}
