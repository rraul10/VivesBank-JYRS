package jyrs.dev.vivesbank.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class IdGenerator {

        public static String generateHash() {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[8];
            random.nextBytes(bytes);
            String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            return base64.replace('+', '-').replace('/', '_');
        }
}
