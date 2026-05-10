package repo;

import model.IslemTuru;
import model.Kategori;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteKategoriRepo implements KategoriRepo {

    private Connection con() throws SQLException {
        return VeriTabaniBaglantisi.getBaglanti();
    }

    @Override
    public void kaydet(Kategori kategori) {
        String sql = "INSERT INTO kategoriler (id, kullanici_id, ad, tur, renk) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kategori.getId());
            ps.setString(2, kategori.getKullaniciId());
            ps.setString(3, kategori.getAd());
            ps.setString(4, kategori.getTur().name());
            ps.setString(5, kategori.getRenk());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kategori kaydet hatası: " + e.getMessage());
        }
    }

    @Override
    public void sil(String kategoriId) {
        String sql = "DELETE FROM kategoriler WHERE id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kategoriId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kategori sil hatası: " + e.getMessage());
        }
    }

    @Override
    public List<Kategori> kullaniciyaGore(String kullaniciId) {
        String sql = "SELECT id, kullanici_id, ad, tur, renk FROM kategoriler WHERE kullanici_id = ?";
        List<Kategori> liste = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kullaniciId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Kategori(
                        rs.getString("id"),
                        rs.getString("kullanici_id"),
                        rs.getString("ad"),
                        IslemTuru.valueOf(rs.getString("tur")),
                        rs.getString("renk")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Kategori listele hatası: " + e.getMessage());
        }
        return liste;
    }
}
