package cl.lte.ae1_abpro;

import java.util.UUID;

public class IdGenerator {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
