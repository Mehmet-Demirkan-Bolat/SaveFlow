package repo;

import model.ButceLimiti;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteButceLimitiRepo implements ButceLimitiRepo {

    private Connection con() throws SQLException {
        return VeriTabaniBaglantisi.getBaglanti();
    }

    @Override
    public void kaydetYaDaGuncelle(ButceLimiti limit) {
        // Önce var mı kontrol et; varsa UPDATE, yoksa INSERT
        Optional<ButceLimiti> mevcut = kategoriIleGetir(limit.getKullaniciId(), limit.getKategoriId());
        if (mevcut.isPresent()) {
            String sql = "UPDATE butce_limitleri SET aylik_limit = ? WHERE kullanici_id = ? AND kategori_id = ?";
            try (PreparedStatement ps = con().prepareStatement(sql)) {
                ps.setDouble(1, limit.getAylikLimit());
                ps.setString(2, limit.getKullaniciId());
                ps.setString(3, limit.getKategoriId());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Bütçe limiti güncelle hatası: " + e.getMessage());
            }
        } else {
            String sql = "INSERT INTO butce_limitleri (id, kullanici_id, kategori_id, aylik_limit) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con().prepareStatement(sql)) {
                ps.setString(1, limit.getId());
                ps.setString(2, limit.getKullaniciId());
                ps.setString(3, limit.getKategoriId());
                ps.setDouble(4, limit.getAylikLimit());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Bütçe limiti kaydet hatası: " + e.getMessage());
            }
        }
    }

    @Override
    public void sil(String id) {
        String sql = "DELETE FROM butce_limitleri WHERE id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Bütçe limiti sil hatası: " + e.getMessage());
        }
    }

    @Override
    public Optional<ButceLimiti> kategoriIleGetir(String kullaniciId, String kategoriId) {
        String sql = "SELECT * FROM butce_limitleri WHERE kullanici_id = ? AND kategori_id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kullaniciId);
            ps.setString(2, kategoriId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ButceLimiti(
                        rs.getString("id"),
                        rs.getString("kullanici_id"),
                        rs.getString("kategori_id"),
                        rs.getDouble("aylik_limit")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Bütçe limiti getir hatası: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<ButceLimiti> kullaniciyaGore(String kullaniciId) {
        List<ButceLimiti> liste = new ArrayList<>();
        String sql = "SELECT * FROM butce_limitleri WHERE kullanici_id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kullaniciId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new ButceLimiti(
                        rs.getString("id"),
                        rs.getString("kullanici_id"),
                        rs.getString("kategori_id"),
                        rs.getDouble("aylik_limit")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Bütçe limitleri listele hatası: " + e.getMessage());
        }
        return liste;
    }
}
