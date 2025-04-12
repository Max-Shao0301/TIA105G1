package com.mfa.service;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Service;

@Service
public class TotpService {
    private final DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();//TOTP 金鑰生成器
    private final DefaultCodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());//TOTP 驗證器(產生TOTP密碼, 獲得當下時間)

    // 產生金鑰
    public String generateSecret() {
        return secretGenerator.generate();
    }

    // 驗證密碼
    public boolean verifyCode(String secret, String code) {
        return verifier.isValidCode(secret, code);
    }

    // 產生 TOTP金鑰的QR Code裡面放TOTP金鑰，帳戶名稱，發行者名稱
    public String getQRCodeImage(String secret, String account, String issuer)throws Exception {
        // 使用QrData來建立QR Code的資料
        QrData data = new QrData.Builder()
                .label(account)
                .secret(secret)
                .issuer(issuer)
                .build();

        ZxingPngQrGenerator generator = new ZxingPngQrGenerator(); // 使用 ZXing 生成 QR Code
        byte[] qrCode = generator.generate(data);// 產生QR Code
        return "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(qrCode); // 將QR Code轉換成Base64字串
    }
}

