package repo;

import model.HedefTasarruf;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHedefTasarrufRepo implements HedefTasarrufRepo {

    private Connection con() throws SQLException {
        return VeriTabaniBaglantisi.getBaglanti();
    }

    @Override
    public void kaydet(HedefTasarruf hedef) {
        String sql = """
            INSERT INTO hedef_tasarruf
              (id, kullanici_id, ad, hedef_miktar, birikilen_miktar, baslangic_tarihi, bitis_tarihi)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, hedef.getId());
            ps.setString(2, hedef.getKullaniciId());
            ps.setString(3, hedef.getAd());
            ps.setDouble(4, hedef.getHedefMiktar());
            ps.setDouble(5, hedef.getBirikilenMiktar());
            ps.setString(6, hedef.getBaslangicTarihi().toString());
            ps.setString(7, hedef.getBitisTarihi().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Hedef kaydet hatası: " + e.getMessage());
        }
    }

    @Override
    public void guncelle(HedefTasarruf hedef) {
        String sql = """
            UPDATE hedef_tasarruf
            SET ad = ?, hedef_miktar = ?, birikilen_miktar = ?, bitis_tarihi = ?
            WHERE id = ?
        """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, hedef.getAd());
            ps.setDouble(2, hedef.getHedefMiktar());
            ps.setDouble(3, hedef.getBirikilenMiktar());
            ps.setString(4, hedef.getBitisTarihi().toString());
            ps.setString(5, hedef.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Hedef güncelle hatası: " + e.getMessage());
        }
    }

    @Override
    public void katki(String hedefId, double miktar) {
        String sql = "UPDATE hedef_tasarruf SET birikilen_miktar = birikilen_miktar + ? WHERE id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setDouble(1, miktar);
            ps.setString(2, hedefId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Hedef katkı hatası: " + e.getMessage());
        }
    }

    @Override
    public void sil(String id) {
        String sql = "DELETE FROM hedef_tasarruf WHERE id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Hedef sil hatası: " + e.getMessage());
        }
    }

    @Override
    public List<HedefTasarruf> kullaniciyaGore(String kullaniciId) {
        List<HedefTasarruf> liste = new ArrayList<>();
        String sql = "SELECT * FROM hedef_tasarruf WHERE kullanici_id = ? ORDER BY bitis_tarihi ASC";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, kullaniciId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new HedefTasarruf(
                        rs.getString("id"),
                        rs.getString("kullanici_id"),
                        rs.getString("ad"),
                        rs.getDouble("hedef_miktar"),
                        rs.getDouble("birikilen_miktar"),
                        LocalDate.parse(rs.getString("baslangic_tarihi")),
                        LocalDate.parse(rs.getString("bitis_tarihi"))
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Hedefler listele hatası: " + e.getMessage());
        }
        return liste;
    }
}
