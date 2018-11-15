import java.math.BigInteger;
import java.util.Date;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CryptUtil {
	
	/**
	 * 処理時間を計測しながらBcyptでハッシュ化
	 * @param passStr パスワード文字列
	 * @param strength ハッシュ化の回数。4～31（2の何乗か）
	 */
	public static String testBcryptEncode(String passStr, int strength) {
		
		// 開始時刻
		long startTime = (new Date()).getTime();
		
		// BCryptでのハッシュ化
		BCryptPasswordEncoder bpe = new BCryptPasswordEncoder(strength);
		String hashStr = bpe.encode(passStr);
		
		// ハッシュ化終了時刻
		long endTime = (new Date()).getTime();
		String timeDelta = String.format("%.4f", ((float)(endTime - startTime) / 1000));
		
	    System.out.println("encoding time  : " + timeDelta + " sec.");
	    
	    return hashStr;
	}
	
	/**
	 * 処理時間を計測しながら生パスワードとハッシュが合致するかの認証
	 * @param passStr 生
	 * @param hashStr ハッシュ
	 * @param strength
	 */
	public static void testBcyptVerify(String passStr, String hashStr, int strength) {
		
		// 開始時刻
		long startTime = (new Date()).getTime();
		
		// パスワードがハッシュに一致するかのチェック
		BCryptPasswordEncoder bpe = new BCryptPasswordEncoder(strength);
		boolean isMatch = bpe.matches(passStr, hashStr);
		
		// 検証終了時刻
		long endTime = (new Date()).getTime();
		String timeDelta = String.format("%.3f", ((float)(endTime - startTime) / 1000));
		
		if (isMatch) {
		    System.out.println("verifying time : " + timeDelta + " sec.");
		} else {
			
		}
	}
	
	public static void main(String[] args) {
		
		String passStr = "password_desu_4";
		String hashStr;
		
		// 単純なエンコードと認証
		BCryptPasswordEncoder bpe = new BCryptPasswordEncoder();
		hashStr = bpe.encode(passStr);
		System.out.println("original string   : " + passStr);
		System.out.println("encoded by Bcrypt : " + hashStr);
		
		boolean isMatch = bpe.matches(passStr, hashStr);
		System.out.println("result            : " + (isMatch ? "matche" : "not matche"));		
		
		
		System.out.println("");
		
		
		// strengthごとに時間計測
		double streachTimes; 

		// 4から31だけど31までやると一週間くらいかかりそう
		// for (int strength=4; strength<=31; strength++) {
		for (int strength=4; strength<=20; strength++) {
			streachTimes = Math.pow(2, strength);
			System.out.println("= strength : " + strength + ", streach times : " + String.format("%1$.0f", streachTimes) + " ========");
			hashStr = testBcryptEncode(passStr, strength);
			testBcyptVerify(passStr, hashStr, strength);
			System.out.println("");
		}
	}

}
