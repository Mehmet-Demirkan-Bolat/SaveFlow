package repo;

import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SQLiteTekrarlananIslemRepo implements TekrarlananIslemRepo {

    private Connection con() throws SQLException {
        return VeriTabaniBaglantisi.getBaglanti();
    }

    @Override
    public void kaydet(TekrarlananIslem islem) {
        String sql = """
            INSERT INTO tekrarlanan_islemler
              (id, kullanici_id, baslik, miktar, kategori_id, tur, ekstra, tekrar_tipi, sonraki_tarih, aktif)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, islem.getId());
            ps.setString(2, islem.getKullaniciId());
            ps.setString(3, islem.getBaslik());
            ps.setDouble(4, islem.getMiktar());
            ps.setString(5, islem.getKategori().getId());
            ps.setString(6, islem.getTur().name());
            ps.setString(7, islem.getEkstra());
            ps.setString(8, islem.getTekrarTipi().name());
            ps.setString(9, islem.getSonrakiTarih().toString());
            ps.setInt(10, islem.isAktif() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Tekrarlanan işlem kaydet hatası: " + e.getMessage());
        }
    }

    @Override
    public void guncelle(TekrarlananIslem islem) {
        String sql = """
            UPDATE tekrarlanan_islemler
            SET sonraki_tarih = ?, aktif = ?
            WHERE id = ?
        """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, islem.getSonrakiTarih().toString());
            ps.setInt(2, islem.isAktif() ? 1 : 0);
            ps.setString(3, islem.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Tekrarlanan işlem güncelle hatası: " + e.getMessage());
        }
    }

    @Override
    public void sil(String id) {
        String sql = "DELETE FROM tekrarlanan_islemler WHERE id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Tekrarlanan işlem sil hatası: " + e.getMessage());
        }
    }

    @Override
    public List<TekrarlananIslem> kullaniciyaGore(String kullaniciId) {
        String sql = """
            SELECT tr.*, k.id AS kat_id, k.kullanici_id AS kat_uid, k.ad AS kat_ad,
                   k.tur AS kat_tur, k.renk AS kat_renk
            FROM tekrarlanan_islemler tr
            JOIN kategoriler k ON tr.kategori_id = k.id
            WHERE tr.kullanici_id = ?
            ORDER BY tr.sonraki_tarih ASC
        """;
        return sorguCalistir(sql, kullaniciId, null);
    }

    @Override
    public List<TekrarlananIslem> vadesiGelenler(String kullaniciId, LocalDate bugun) {
        String sql = """
            SELECT tr.*, k.id AS kat_id, k.kullanici_id AS kat_uid, k.ad AS kat_ad,
                   k.tur AS kat_tur, k.renk AS kat_renk
            FROM tekrarlanan_islemler tr
            JOIN kategoriler k ON tr.kategori_id = k.id
            WHERE tr.kullanici_id = ? AND tr.aktif = 1 AND tr.sonraki_tarih <= ?
            ORDER BY tr.sonraki_tarih ASC
        """;
        return sorguCalistir(sql, kullaniciId, bugun);
    }

    private List<TekrarlananIslem> sorguCalistir(String sql, String kullaniciId, LocalDate tarih) {
        List<TekrarlananIslem> liste = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kullaniciId);
            if (tarih != null) ps.setString(2, tarih.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Kategori kat = new Kategori(
                        rs.getString("kat_id"),
                        rs.getString("kat_uid"),
                        rs.getString("kat_ad"),
                        IslemTuru.valueOf(rs.getString("kat_tur")),
                        rs.getString("kat_renk")
                    );
                    liste.add(new TekrarlananIslem(
                        rs.getString("id"),
                        rs.getString("kullanici_id"),
                        rs.getString("baslik"),
                        rs.getDouble("miktar"),
                        kat,
                        IslemTuru.valueOf(rs.getString("tur")),
                        rs.getString("ekstra"),
                        TekrarlananIslem.TekrarTipi.valueOf(rs.getString("tekrar_tipi")),
                        LocalDate.parse(rs.getString("sonraki_tarih")),
                        rs.getInt("aktif") == 1
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Tekrarlanan işlem sorgu hatası: " + e.getMessage());
        }
        return liste;
    }
}
