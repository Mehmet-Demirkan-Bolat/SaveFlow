package repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class VeriTabaniBaglantisi {

    private static final String DB_URL = "jdbc:sqlite:saveflow.db";
    private static Connection baglanti = null;

    public static Connection getBaglanti() throws SQLException {
        if (baglanti == null || baglanti.isClosed()) {
            // Sürücüyü açıkça yükle — bu satır olmadan "No suitable driver" hatası çıkar
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC sürücüsü bulunamadı. " +
                    "sqlite-jdbc-3.14.2.jar dosyasını Build Path'e ekleyin.", e);
            }
            baglanti = DriverManager.getConnection(DB_URL);
            baglanti.setAutoCommit(true);
            tablolariOlustur(baglanti);
        }
        return baglanti;
    }

    private static void tablolariOlustur(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS kullanicilar (
                    id      TEXT PRIMARY KEY,
                    ad      TEXT NOT NULL,
                    eposta  TEXT NOT NULL UNIQUE,
                    sifre   TEXT NOT NULL
                )
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS kategoriler (
                    id           TEXT PRIMARY KEY,
                    kullanici_id TEXT NOT NULL,
                    ad           TEXT NOT NULL,
                    tur          TEXT NOT NULL,
                    renk         TEXT DEFAULT '#3B82F6'
                )
            """);
            // Mevcut DB'ye renk sütunu ekle (yoksa)
            try { st.executeUpdate("ALTER TABLE kategoriler ADD COLUMN renk TEXT DEFAULT '#3B82F6'"); }
            catch (java.sql.SQLException ignore) {}

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS islemler (
                    id           TEXT PRIMARY KEY,
                    kullanici_id TEXT NOT NULL,
                    tarih        TEXT NOT NULL,
                    miktar       REAL NOT NULL,
                    aciklama     TEXT,
                    kategori_id  TEXT NOT NULL,
                    tur          TEXT NOT NULL,
                    ekstra       TEXT,
                    FOREIGN KEY (kullanici_id) REFERENCES kullanicilar(id),
                    FOREIGN KEY (kategori_id)  REFERENCES kategoriler(id)
                )
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS butce_limitleri (
                    id           TEXT PRIMARY KEY,
                    kullanici_id TEXT NOT NULL,
                    kategori_id  TEXT NOT NULL,
                    aylik_limit  REAL NOT NULL,
                    UNIQUE(kullanici_id, kategori_id)
                )
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tekrarlanan_islemler (
                    id            TEXT PRIMARY KEY,
                    kullanici_id  TEXT NOT NULL,
                    baslik        TEXT NOT NULL,
                    miktar        REAL NOT NULL,
                    kategori_id   TEXT NOT NULL,
                    tur           TEXT NOT NULL,
                    ekstra        TEXT,
                    tekrar_tipi   TEXT NOT NULL,
                    sonraki_tarih TEXT NOT NULL,
                    aktif         INTEGER NOT NULL DEFAULT 1
                )
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS hedef_tasarruf (
                    id               TEXT PRIMARY KEY,
                    kullanici_id     TEXT NOT NULL,
                    ad               TEXT NOT NULL,
                    hedef_miktar     REAL NOT NULL,
                    birikilen_miktar REAL NOT NULL DEFAULT 0,
                    baslangic_tarihi TEXT NOT NULL,
                    bitis_tarihi     TEXT NOT NULL
                )
            """);
        }
    }
}
