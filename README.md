# Sistem Manajemen Data Buku - Remedial UCP 2

Aplikasi Android berbasis Kotlin untuk sistem manajemen data buku universitas dengan fitur-fitur canggih.

## Fitur Utama

### 1. Database Schema
- **Book**: Entity untuk buku fisik dengan ID unik (physicalId)
- **Author**: Entity untuk pengarang
- **BookAuthor**: Junction table (RAT) untuk relasi many-to-many antara Book dan Author
- **Category**: Entity dengan struktur hierarkis (kedalaman tak terbatas)
- **AuditLog**: Entity untuk menyimpan rekaman perubahan data

### 2. Pencarian Rekursif
- Pencarian buku berdasarkan kategori secara rekursif
- Mencari pada kategori induk akan menampilkan semua buku di sub-kategori turunannya
- Menggunakan CTE (Common Table Expression) dengan WITH RECURSIVE

### 3. Validasi Data
- Validasi ketat berdasarkan tipe data untuk semua input
- Validasi ISBN (10 atau 13 digit)
- Validasi email
- Validasi Physical ID (format alphanumeric dengan underscore/hyphen)
- Validasi status buku (available, borrowed, maintenance, deleted)

### 4. Deteksi Cyclic Reference
- Mencegah cyclic reference pada struktur kategori
- Deteksi otomatis sebelum insert/update kategori
- Menggunakan algoritma DFS untuk deteksi cycle

### 5. Audit Logging
- Menyimpan rekaman data sebelum dan sesudah perubahan
- Log untuk operasi INSERT, UPDATE, DELETE, dan SOFT_DELETE
- Format JSON untuk data sebelum/sesudah

### 6. Soft Delete
- Implementasi soft delete untuk semua entitas
- Data tidak dihapus permanen, hanya ditandai sebagai deleted
- Timestamp deletedAt untuk tracking

### 7. Complex Deletion Logic dengan Transaction
- Jika menghapus kategori dengan buku berstatus "borrowed" → **ROLLBACK otomatis**
- Jika tidak ada buku borrowed → opsi dinamis:
  - Soft delete semua buku dalam kategori, atau
  - Pindahkan buku ke "Tanpa Kategori" (categoryId = NULL)
- Menggunakan database transaction untuk memastikan atomicity

### 8. Asynchronous Operations
- Semua operasi database berjalan secara asynchronous
- Menggunakan Kotlin Coroutines dan Flow
- Aplikasi tetap responsif selama operasi berjalan

### 9. Pagination
- Support pagination untuk performa optimal
- Membatasi data yang dimuat pada perangkat pengguna
- Query dengan LIMIT dan OFFSET

## Teknologi yang Digunakan

- **Kotlin**: Bahasa pemrograman utama
- **Jetpack Compose**: UI framework modern
- **Room Database**: Persistence layer dengan SQLite
- **Coroutines & Flow**: Asynchronous programming
- **ViewModel**: State management
- **Navigation Compose**: Navigation antara screens
- **Material Design 3**: Design system

## Struktur Project

```
app/src/main/java/com/example/manajemendatabuku/
├── data/
│   ├── model/          # Entity classes (Book, Author, Category, etc.)
│   ├── dao/            # Data Access Objects
│   ├── database/       # Room Database setup
│   └── repository/     # Repository layer dengan business logic
├── ui/
│   ├── screen/         # Compose screens
│   ├── viewmodel/      # ViewModels untuk state management
│   ├── navigation/     # Navigation setup
│   └── theme/          # Material theme
└── util/               # Utility classes (Validation, Audit, etc.)
```

## Persyaratan Sistem

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **SQLite**: Versi dengan dukungan WITH RECURSIVE (SQLite 3.8.3+)
  - Android API 16+ sudah mendukung
  - Room 2.6.1 kompatibel dengan fitur ini

## Cara Menggunakan

### Menambah Buku
1. Navigasi ke "Buku" dari home screen
2. Klik "Tambah"
3. Isi form dengan data buku
4. Physical ID harus unik
5. Validasi otomatis akan memeriksa input

### Menambah Kategori
1. Navigasi ke "Kategori"
2. Klik "Tambah"
3. Isi nama dan deskripsi kategori
4. Sistem akan mencegah cyclic reference

### Menghapus Kategori
1. Pilih kategori yang akan dihapus
2. Sistem akan:
   - Cek apakah ada buku dengan status "borrowed"
   - Jika ada → rollback dan tampilkan error
   - Jika tidak → tanyakan apakah buku ikut dihapus atau dipindah ke "Tanpa Kategori"

### Pencarian Rekursif
- Saat mencari buku berdasarkan kategori, sistem secara otomatis akan mencari di semua sub-kategori
- Tidak perlu mencari manual di setiap level

## Catatan Penting

1. **Database Transaction**: Room secara otomatis akan rollback jika terjadi exception dalam transaction
2. **Soft Delete**: Data yang dihapus masih ada di database dengan flag `isDeleted = true`
3. **Audit Log**: Semua perubahan dicatat untuk keamanan dan compliance
4. **Validasi**: Semua input divalidasi sebelum disimpan ke database
5. **Cyclic Reference**: Sistem mencegah kategori menjadi parent dari dirinya sendiri (langsung atau tidak langsung)

## Migrasi Data

Untuk migrasi data besar-besaran dari sistem lama:
- Gunakan fungsi `insertBooks()`, `insertAuthors()`, `insertCategories()` dengan batch insert
- Operasi berjalan secara asynchronous
- Progress dapat ditrack melalui Flow

## Lisensi

Project ini dibuat untuk keperluan remedial UCP 2.
