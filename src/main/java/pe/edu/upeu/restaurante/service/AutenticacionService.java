package pe.edu.upeu.restaurante.service;

import pe.edu.upeu.restaurante.model.Perfil;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class AutenticacionService {
    private static final int ITERACIONES = 600_000;
    private static final HexFormat HEX = HexFormat.of();
    private final Map<String, Cuenta> cuentas = Map.of(
        "admin", new Cuenta(Perfil.ADMINISTRACION, "4af76a5c4a9c51d9a9063b1b46d69c39",
            "a095722a23376686e3a1065e22cd563f516ba28ac213a60791e36b94bc593cf6"),
        "atencion", new Cuenta(Perfil.ATENCION, "2022771e5d3edc3450f4392ffa054fce",
            "502192fc4c921f77365ab1bd518e3cb35e04cc4d68f4281a2bd3d75da3a2739a")
    );

    public Optional<Perfil> identificar(String usuario, char[] clave) {
        Cuenta cuenta = cuentas.get(usuario.strip().toLowerCase(Locale.ROOT));
        if (cuenta == null || clave.length == 0) {
            return Optional.empty();
        }

        // Cada cuenta de demostración tiene su propia sal y su propio hash.
        PBEKeySpec parametros = new PBEKeySpec(clave, HEX.parseHex(cuenta.sal), ITERACIONES, 256);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(parametros).getEncoded();
            return MessageDigest.isEqual(hash, HEX.parseHex(cuenta.hash))
                ? Optional.of(cuenta.perfil) : Optional.empty();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo verificar la contraseña.", e);
        } finally {
            parametros.clearPassword();
        }
    }

    private static class Cuenta {
        private final Perfil perfil;
        private final String sal;
        private final String hash;

        private Cuenta(Perfil perfil, String sal, String hash) {
            this.perfil = perfil;
            this.sal = sal;
            this.hash = hash;
        }
    }
}
