package repo;

import exception.KayitHatasi;
import model.Kullanici;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLiteKullaniciRepo implements KullaniciRepo {

    /** Bağlantı yardımcısını kısaltma */
    private Connection con() throws SQLException {
        return VeriTabaniBaglantisi.getBaglanti();
    }

    @Override
    public void kaydet(Kullanici kullanici) throws KayitHatasi {
        String sql = "INSERT INTO kullanicilar (id, ad, eposta, sifre) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kullanici.getId());
            ps.setString(2, kullanici.getAd());
            ps.setString(3, kullanici.getEposta());
            ps.setString(4, kullanici.getSifre());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new KayitHatasi("Kullanıcı kaydedilemedi: " + e.getMessage());
        }
    }

    @Override
    public Optional<Kullanici> emailIleGetir(String email) {
        String sql = "SELECT id, ad, eposta, sifre FROM kullanicilar WHERE eposta = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Kullanici(
                        rs.getString("id"),
                        rs.getString("ad"),
                        rs.getString("eposta"),
                        rs.getString("sifre")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("emailIleGetir hatası: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean emailMevcutMu(String email) {
        String sql = "SELECT COUNT(*) FROM kullanicilar WHERE eposta = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("emailMevcutMu hatası: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void sil(String kullaniciId) {
        String sql = "DELETE FROM kullanicilar WHERE id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kullaniciId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kullanıcı sil hatası: " + e.getMessage());
        }
    }
}

