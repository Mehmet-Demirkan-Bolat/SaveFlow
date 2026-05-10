# 💰 SaveFlow

> Kişisel bütçe ve tasarruf yönetim uygulaması — Java & Swing ile geliştirilmiştir.

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)
![SQLite](https://img.shields.io/badge/SQLite-3.x-blue?style=flat-square&logo=sqlite)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-brightgreen?style=flat-square)

---

## 📖 Proje Hakkında

**SaveFlow**, kullanıcıların gelir ve giderlerini takip etmesine, bütçe limitleri belirlemesine ve tasarruf hedefleri oluşturmasına olanak tanıyan masaüstü bir kişisel finans uygulamasıdır. OOP (Nesne Yönelimli Programlama) dersi kapsamında Java ile geliştirilmiştir.

Uygulama; temiz katmanlı mimari (Repository → Service → Controller → UI), bağımlılık enjeksiyonu (Dependency Injection) ve MVC deseni kullanılarak inşa edilmiştir.

---

## ✨ Özellikler

| Özellik | Açıklama |
|---|---|
| 👤 Kullanıcı Yönetimi | Kayıt, giriş ve oturum yönetimi |
| 💳 Gelir / Gider Takibi | Kategorili işlem ekleme, düzenleme ve silme |
| 📊 Analiz & Raporlama | Aylık bazlı gelir-gider analizi, pasta ve çubuk grafikler |
| 🏷️ Kategori Yönetimi | Özel gelir/gider kategorileri oluşturma |
| 📅 Tekrarlanan İşlemler | Otomatik periyodik işlem tanımlama |
| 🎯 Tasarruf Hedefleri | Hedef belirleme ve ilerleme takibi |
| ⚠️ Bütçe Limitleri | Kategori bazlı aylık harcama limiti ve aşım uyarıları |
| 🌙 Modern Tema | Koyu renk paleti ile şık Swing arayüzü |

---

## 🏗️ Mimari

Proje, klasik **çok katmanlı (N-Tier)** mimari üzerine kuruludur:

```
┌─────────────────────────────────────────┐
│                UI Katmanı               │  ← Swing formlar ve ekranlar
├─────────────────────────────────────────┤
│           Controller Katmanı            │  ← İş akışı koordinasyonu
├─────────────────────────────────────────┤
│            Service Katmanı              │  ← İş mantığı & doğrulama
├─────────────────────────────────────────┤
│           Repository Katmanı            │  ← Veri erişim arayüzleri
├─────────────────────────────────────────┤
│         SQLite (Kalıcı Depolama)        │  ← sqlite-jdbc ile yerel DB
└─────────────────────────────────────────┘
```

### Paket Yapısı

```
src/
├── app/                    # Giriş noktası (Main.java — DI bağlantısı)
├── controller/             # Controller katmanı
│   ├── ButceDenetleyici
│   ├── HedefTasarrufDenetleyici
│   ├── IslemDenetleyici
│   ├── KategoriDenetleyici
│   ├── KullaniciDenetleyici
│   └── TekrarlananIslemDenetleyici
├── service/                # İş mantığı katmanı
│   ├── AnalizServisi
│   ├── ButceServisi
│   ├── HedefTasarrufServisi
│   ├── IslemServisi
│   ├── KategoriServisi
│   ├── KullaniciServisi
│   ├── OturumYoneticisi
│   ├── RaporServisi
│   └── TekrarlananIslemServisi
├── repo/                   # Repository arayüzleri & SQLite uygulamaları
│   ├── *Repo.java          # Arayüzler
│   └── SQLite*Repo.java    # Somut SQLite implementasyonları
├── model/                  # Domain modelleri
│   ├── Islem / Gelir / Gider
│   ├── Kategori / IslemTuru
│   ├── ButceLimiti / ButceAsimi
│   ├── HedefTasarruf
│   └── TekrarlananIslem
├── ui/                     # Swing arayüz bileşenleri
│   ├── AnaEkran.java
│   ├── GirisEkrani.java / KayitEkrani.java
│   ├── IslemFormu.java
│   ├── RaporEkrani.java
│   ├── HedefTasarrufPanel.java
│   ├── ButceLimitiFormu.java
│   ├── TekrarlananIslemFormu.java
│   └── ModernTema.java
├── exception/              # Özel istisnalar
│   ├── GecersizMiktarHatasi
│   ├── GirisHatasi
│   └── KayitHatasi
└── util/                   # Yardımcı sınıflar
    └── DogrulamaYardimcisi
lib/
└── sqlite-jdbc.jar         # SQLite sürücüsü
```

---

## 🛠️ Gereksinimler

- **Java** 17 veya üzeri
- **Eclipse IDE** (önerilen) veya herhangi bir Java IDE
- **SQLite JDBC** sürücüsü (`lib/sqlite-jdbc.jar` — repoda mevcut)

---

## 🚀 Kurulum & Çalıştırma

### 1. Repoyu Klonla

```bash
git clone https://github.com/kullanici-adi/SaveFlow.git
cd SaveFlow
```

### 2. Eclipse ile Aç

1. **File → Import → Existing Projects into Workspace** seçin.
2. Klonladığınız `SaveFlow` klasörünü gösterin ve **Finish** deyin.

### 3. Build Path Kontrolü

SQLite sürücüsü `lib/sqlite-jdbc.jar` olarak zaten mevcuttur. Eclipse otomatik algılamazsa:

> **Proje → sağ tık → Build Path → Configure Build Path → Libraries → Add JARs → lib/sqlite-jdbc.jar**

### 4. Çalıştır

`src/app/Main.java` dosyasına sağ tıklayıp **Run As → Java Application** seçin.

Uygulama ilk çalıştırmada `saveflow.db` adlı SQLite veritabanını otomatik olarak oluşturur.

---

## 📐 Tasarım Desenleri & OOP İlkeleri

- **Dependency Injection** — Tüm bağımlılıklar `Main.java` içinde elle enjekte edilir; alt katmanlar `new` kullanmaz.
- **Repository Pattern** — Veri erişim mantığı arayüzler arkasında soyutlanmıştır; SQLite yerine başka bir DB'ye geçmek kolaydır.
- **MVC** — UI, Controller ve Model katmanları birbirinden bağımsızdır.
- **Singleton Benzeri Yönetim** — `OturumYoneticisi` tekil nesne olarak tüm katmanlara paylaşılır.
- **Arayüz Sözleşmeleri** — `AnalizYapilabilir`, `Raporlanabilir` gibi arayüzler ile gevşek bağlılık sağlanmıştır.

---

## 🖼️ Ekran Görüntüleri

> *(Ekran görüntülerini `docs/screenshots/` klasörüne ekleyerek buraya bağlantı verebilirsiniz.)*

---

## 🤝 Katkıda Bulunma

1. Bu repoyu fork'layın
2. Yeni bir dal oluşturun: `git checkout -b ozellik/yeni-ozellik`
3. Değişikliklerinizi commit edin: `git commit -m 'feat: yeni özellik eklendi'`
4. Dalı push edin: `git push origin ozellik/yeni-ozellik`
5. Pull Request açın

---

## 📄 Lisans

Bu proje MIT Lisansı kapsamında lisanslanmıştır. Detaylar için `LICENSE` dosyasına bakın.

---

<p align="center">
  OOP Dersi Projesi — Java & Swing ile geliştirilmiştir 🚀
</p>
