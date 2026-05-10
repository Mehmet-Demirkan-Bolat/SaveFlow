package repo;

import model.Gelir;
import model.Gider;
import model.Islem;
import model.IslemTuru;
import model.Kategori;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SQLiteIslemRepo implements IslemRepo {

    private Connection con() throws SQLException {
        return VeriTabaniBaglantisi.getBaglanti();
    }

    @Override
    public void kaydet(Islem islem) {
        String sql = """
            INSERT INTO islemler (id, kullanici_id, tarih, miktar, aciklama, kategori_id, tur, ekstra)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, islem.getId());
            ps.setString(2, islem.getKullaniciId());
            ps.setString(3, islem.getTarih().toString());
            ps.setDouble(4, islem.getMiktar());
            ps.setString(5, islem.getAciklama());
            ps.setString(6, islem.getKategori().getId());
            ps.setString(7, islem.getTur().name());
            // Gelir için kaynak, Gider için ödeme yöntemi
            if (islem instanceof Gelir g) {
                ps.setString(8, g.getKaynak());
            } else if (islem instanceof Gider gd) {
                ps.setString(8, gd.getOdemeYontemi());
            } else {
                ps.setString(8, null);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("İşlem kaydet hatası: " + e.getMessage());
        }
    }

    @Override
    public void guncelle(String islemId, double miktar, LocalDate tarih,
                         String aciklama, Kategori kategori, String ekstra, model.IslemTuru tur) {
        String sql = """
            UPDATE islemler
            SET miktar = ?, tarih = ?, aciklama = ?, kategori_id = ?, ekstra = ?, tur = ?
            WHERE id = ?
        """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setDouble(1, miktar);
            ps.setString(2, tarih.toString());
            ps.setString(3, aciklama);
            ps.setString(4, kategori.getId());
            ps.setString(5, ekstra);
            ps.setString(6, tur.name());
            ps.setString(7, islemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("İşlem güncelle hatası: " + e.getMessage());
        }
    }

    @Override
    public void sil(String islemId) {
        String sql = "DELETE FROM islemler WHERE id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, islemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("İşlem sil hatası: " + e.getMessage());
        }
    }

    @Override
    public List<Islem> kullaniciyaGore(String kullaniciId) {
        String sql = """
            SELECT i.id, i.kullanici_id, i.tarih, i.miktar, i.aciklama, i.tur, i.ekstra,
                   k.id AS kat_id, k.kullanici_id AS kat_uid, k.ad AS kat_ad, k.tur AS kat_tur,
                   k.renk AS kat_renk
            FROM islemler i
            JOIN kategoriler k ON i.kategori_id = k.id
            WHERE i.kullanici_id = ?
            ORDER BY i.tarih DESC
        """;
        return sorguCalistir(sql, kullaniciId, null, null);
    }

    @Override
    public List<Islem> tarihAraliginaGore(String kullaniciId, LocalDate bas, LocalDate bit) {
        String sql = """
            SELECT i.id, i.kullanici_id, i.tarih, i.miktar, i.aciklama, i.tur, i.ekstra,
                   k.id AS kat_id, k.kullanici_id AS kat_uid, k.ad AS kat_ad, k.tur AS kat_tur,
                   k.renk AS kat_renk
            FROM islemler i
            JOIN kategoriler k ON i.kategori_id = k.id
            WHERE i.kullanici_id = ? AND i.tarih BETWEEN ? AND ?
            ORDER BY i.tarih DESC
        """;
        return sorguCalistir(sql, kullaniciId, bas, bit);
    }

    /**
     * Ortak sorgu çalıştırma yardımcısı.
     * bas ve bit null ise tek parametre (kullaniciId) kullanılır.
     */
    private List<Islem> sorguCalistir(String sql, String kullaniciId,
                                      LocalDate bas, LocalDate bit) {
        List<Islem> liste = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kullaniciId);
            if (bas != null && bit != null) {
                ps.setString(2, bas.toString());
                ps.setString(3, bit.toString());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Kategori kat = new Kategori(
                        rs.getString("kat_id"),
                        rs.getString("kat_uid"),
                        rs.getString("kat_ad"),
                        IslemTuru.valueOf(rs.getString("kat_tur")),
                        rs.getString("kat_renk")
                    );
                    String id        = rs.getString("id");
                    String uid       = rs.getString("kullanici_id");
                    LocalDate tarih  = LocalDate.parse(rs.getString("tarih"));
                    double miktar    = rs.getDouble("miktar");
                    String aciklama  = rs.getString("aciklama");
                    String tur       = rs.getString("tur");
                    String ekstra    = rs.getString("ekstra");

                    Islem islem;
                    if ("GELIR".equals(tur)) {
                        islem = new Gelir(id, uid, tarih, miktar, aciklama, kat, ekstra);
                    } else {
                        islem = new Gider(id, uid, tarih, miktar, aciklama, kat, ekstra);
                    }
                    liste.add(islem);
                }
            }
        } catch (SQLException e) {
            System.err.println("İşlem sorgu hatası: " + e.getMessage());
        }
        return liste;
    }
}

