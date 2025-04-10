package com.mfa;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Service;

@Service
public class TotpService {
    private final DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final DefaultCodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());

    public String generateSecret() {
        return secretGenerator.generate();
    }


    public boolean verifyCode(String secret, String code) {
        return verifier.isValidCode(secret, code);
    }

    public String getQRCodeImage(String secret, String account, String issuer)throws Exception {
        QrData data = new QrData.Builder()
                .label(account)
                .secret(secret)
                .issuer(issuer)
                .build();

        ZxingPngQrGenerator generator = new ZxingPngQrGenerator();
        byte[] qrCode = generator.generate(data);
        return "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(qrCode);
    }
}

