import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OTPManager {
    private static final Map<String, OTPData> otpStore = new HashMap<>();
    private static final Random random = new Random();
    private static final long OTP_VALIDITY_MINUTES = 5;

    public static class OTPData {
        private final String otp;
        private final long timestamp;
        private final String email;

        public OTPData(String otp, String email) {
            this.otp = otp;
            this.email = email;
            this.timestamp = System.currentTimeMillis();
        }

        public String getOtp() {
            return otp;
        }

        public String getEmail() {
            return email;
        }

        public boolean isExpired() {
            long currentTime = System.currentTimeMillis();
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - timestamp);
            return diffInMinutes > OTP_VALIDITY_MINUTES;
        }
    }

    public static String generateOTP() {
        return String.format("%06d", random.nextInt(999999));
    }

    public static void storeOTP(String userId, String otp, String email) {
        otpStore.put(userId, new OTPData(otp, email));
    }

    public static OTPData getOTP(String userId) {
        OTPData data = otpStore.get(userId);
        if (data != null && data.isExpired()) {
            otpStore.remove(userId);
            return null;
        }
        return data;
    }

    public static boolean verifyOTP(String userId, String enteredOtp) {
        OTPData data = getOTP(userId);
        if (data != null && data.getOtp().equals(enteredOtp)) {
            otpStore.remove(userId); // Remove after successful verification
            return true;
        }
        return false;
    }

    public static void clearExpiredOTPs() {
        otpStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}