package com.ivan.kasirku;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;



import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PengaturanActivity extends AppCompatActivity {

    ListView lvdata;
    Dblocalhelper dbo;
    int printcount = 1;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);
        lvdata = findViewById(R.id.lvdata);
        sp = getApplicationContext().getSharedPreferences("config", 0);
        ed = sp.edit();
        dbo = new Dblocalhelper(this);
        loaddatalist();
    }

    private void loaddatalist() {

        List<Listviewglobaladapter.listglobalmodel> ls = new ArrayList<>();
        ls.add(new Listviewglobaladapter.listglobalmodel("0", "Profil Usaha", "Deskripsikan informasi usaha sebagai info untuk pelanggan anda"));
        ls.add(new Listviewglobaladapter.listglobalmodel("1", "Profile Pengguna", "Atur siapa saja yang boleh menggunakan aplikasi beserta hak aksesnya"));
        ls.add(new Listviewglobaladapter.listglobalmodel("2", "Atur Printer POS", "Atur printer yang anda gunakan untuk mencetak Struck, aplikasi hanya bisa menggunakan printer yag memiliki koneksi bluetooth, Printer Saat ini " + sp.getString("default_printer", "none")));
        ls.add(new Listviewglobaladapter.listglobalmodel("3", "Test Koneksi Printer", "Cek apakah printer anda sudah terkoneksi dan berfungsi dengan baik"));
        ls.add(new Listviewglobaladapter.listglobalmodel("4", "Backup", "Cadangkan data anda untuk mengantisipasi kemungkinan data terhapus, default backup file ada pada folder kasirkubackup di internal storage anda"));
        ls.add(new Listviewglobaladapter.listglobalmodel("5", "Restore", "Pulihkan data yang sudah anda cadangkan, default restore file harus ada di dalam folder kasirkubackup di internal storage anda, pastikan data yang ingin anda pulihkan berada didalam folder tersebut "));

        ls.add(new Listviewglobaladapter.listglobalmodel("6", "Tipe Tampilan Menu Barang", (sp.getInt("view_tipe",0)==0)?"Tampilan Menu List":"Tampilan Menu Grid" ));

        ls.add(new Listviewglobaladapter.listglobalmodel("7", "Tentang Aplikasi", "Aplikasi POS DFPOS V1.0"));
        final ArrayAdapter<String> adapter = new Listviewglobaladapter(this, ls);
        lvdata.setAdapter(adapter);

        lvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if (position == 0) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(com.dfit.dfpos.PengaturanActivity.this);
                    adb.setTitle("Informasi Perusahaan");
                    adb.setCancelable(false);
                    final EditText ednama_usaha = new EditText(com.dfit.dfpos.PengaturanActivity.this);
                    final EditText edalamat_usaha = new EditText(com.dfit.dfpos.PengaturanActivity.this);
                    final EditText ednohp_usaha = new EditText(com.dfit.dfpos.PengaturanActivity.this);
                    final EditText edemail_usaha = new EditText(com.dfit.dfpos.PengaturanActivity.this);
                    final EditText edwebsite = new EditText(com.dfit.dfpos.PengaturanActivity.this);

                    TextInputLayout tilnama_usaha = new TextInputLayout(com.dfit.dfpos.PengaturanActivity.this);
                    tilnama_usaha.addView(ednama_usaha);
                    tilnama_usaha.setHint("Nama Usaha");

                    TextInputLayout tilalamat_usaha = new TextInputLayout(com.dfit.dfpos.PengaturanActivity.this);
                    tilalamat_usaha.addView(edalamat_usaha);
                    tilalamat_usaha.setHint("Alamat");

                    TextInputLayout tilnohp_usaha = new TextInputLayout(com.dfit.dfpos.PengaturanActivity.this);
                    tilnohp_usaha.addView(ednohp_usaha);
                    tilnohp_usaha.setHint("No Handphone");

                    TextInputLayout tilemail = new TextInputLayout(com.dfit.dfpos.PengaturanActivity.this);
                    tilemail.addView(edemail_usaha);
                    tilemail.setHint("Email");

                    TextInputLayout tilweb = new TextInputLayout(com.dfit.dfpos.PengaturanActivity.this);
                    tilweb.addView(edwebsite);
                    tilweb.setHint("Website");

                    LinearLayout ll = new LinearLayout(com.dfit.dfpos.PengaturanActivity.this);
                    ll.setPadding(10, 10, 10, 10);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.addView(tilnama_usaha);
                    ll.addView(tilalamat_usaha);
                    ll.addView(tilnohp_usaha);
                    ll.addView(tilemail);
                    ll.addView(tilweb);
                    adb.setView(ll);


                    SQLiteDatabase db = dbo.getReadableDatabase();
                    Cursor c = db.rawQuery("SELECT nama_usaha,alamat_usaha,nohp_usaha,email_usaha,website FROM perusahaan WHERE id=1", null);
                    if (c.moveToFirst()) {
                        ednama_usaha.setText(c.getString(0));
                        edalamat_usaha.setText(c.getString(1));
                        ednohp_usaha.setText(c.getString(2));
                        edemail_usaha.setText(c.getString(3));
                        edwebsite.setText(c.getString(4));
                    }
                    c.close();
                    db.close();


                    adb.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase db = dbo.getWritableDatabase();
                            db.beginTransaction();
                            try {
                                String nama_usaha = ednama_usaha.getText().toString();
                                String alamat = edalamat_usaha.getText().toString();
                                String nohp = ednohp_usaha.getText().toString();
                                String email = edemail_usaha.getText().toString();
                                String website = edwebsite.getText().toString();
                                db.execSQL("UPDATE perusahaan SET nama_usaha='" + nama_usaha + "', alamat_usaha='" + alamat + "'," +
                                        "nohp_usaha='" + nohp + "',email_usaha='" + email + "',website='" + website + "' WHERE id=1");
                                db.setTransactionSuccessful();
                                Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, "Informasi Berhasil Diperbaharui", Toast.LENGTH_SHORT).show();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                db.endTransaction();
                                db.close();
                            }
                        }
                    });

                    adb.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    adb.show();

                } else if (position == 1) {
                    if (sp.getInt("read_user", 0) == 1) {
                        Intent in = new Intent(com.dfit.dfpos.PengaturanActivity.this, com.dfit.dfpos.PenggunaActivity.class);
                        startActivity(in);

                    } else {
                        Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, "Proses Ditolak, Anda tidak memiliki akses", Toast.LENGTH_SHORT).show();
                    }

                } else if (position == 2) {
                    final BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bdev;
                    if (btadapter.isEnabled()) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(com.ivan.kasirku.PengaturanActivity.this);
                        adb.setTitle("Pilih Perangkat");
                        adb.setCancelable(false);

                        Set<BluetoothDevice> paireddevice = btadapter.getBondedDevices();
                        final List<String> ls = new ArrayList<>();
                        ls.add("none");
                        for (BluetoothDevice btdev : paireddevice) {
                            ls.add(btdev.getName());

                        }
                        int currentprinter = ls.indexOf(sp.getString("default_printer", "none"));
                        final String[] isi = ls.toArray(new String[ls.size()]);
                        adb.setSingleChoiceItems(isi, currentprinter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = dbo.getWritableDatabase();
                                db.beginTransaction();
                                try {
                                    db.execSQL("UPDATE pengaturan SET default_printer='" + isi[which] + "' WHERE id=1");
                                    db.setTransactionSuccessful();
                                    Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, isi[which], Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                    db.close();
                                }
                                ed.putString("default_printer", isi[which].split("---")[0]);
                                ed.apply();


                            }
                        });
                        adb.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.show();
                    } else {
                        Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(in, 5);
                    }

                } else if (position == 3) {
                    BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
                    if (btadapter.isEnabled()) {
                        if (sp.getString("default_printer", "none").equals("none")) {
                            Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, "Tidak ada Printer", Toast.LENGTH_SHORT).show();
                        } else {
                            if (printcount > 1) {
                                Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, "Printer Siap Dipakai", Toast.LENGTH_SHORT).show();
                            } else {
                                printcount = 2;
                                String textprint = "Lorem ipsum dolor sit amet,consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. \n\n\n";
                                byte[] bt = textprint.getBytes();
                                Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, String.valueOf(bt.length), Toast.LENGTH_SHORT).show();
                                Bluetoothprint bprint = new Bluetoothprint(com.ivan.kasirku.PengaturanActivity.this);
                                bprint.print(textprint);
                            }
                        }
                    } else {
                        Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(in, 5);
                    }


                } else if (position == 4) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(com.dfit.dfpos.PengaturanActivity.this);
                    adb.setCancelable(false);
                    adb.setTitle("Informasi");
                    adb.setMessage("Data yang dibackup akan dipindah otomatis ke dalam folder kasirkubackup di internal storage anda, " +
                            "ingat untuk tidak menghapus folder ini," +
                            "data backup bisa anda pindahkan ke Flashdisk, SDCARD atau sejenisnya dengan mengcopy file tersebut, " +
                            "dan untuk merestorenya anda cukup mengcopy file backupnnya ke dalam folder kasirkubackup ");
                    adb.setPositiveButton("Backup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File kasiroffbackup = new File(Environment.getExternalStorageDirectory(), "kasirkubackup");
                            if (ActivityCompat.checkSelfPermission(com.dfit.dfpos.PengaturanActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(com.dfit.dfpos.PengaturanActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(com.dfit.dfpos.PengaturanActivity.this,
                                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                android.Manifest.permission.CAMERA}, 1);
                                if (!kasiroffbackup.exists()) {
                                    kasiroffbackup.mkdirs();
                                }
                                return;
                            }

                            if (!kasiroffbackup.exists()) {
                                kasiroffbackup.mkdirs();
                            }

                            File lokasidb = getDatabasePath("kasirku.db");
                            File lokasibackupdb = new File(Environment.getExternalStorageDirectory(), "kasirkubackup/kasirku.db");
                            File lokasiimage = new File(getFilesDir(), "kasirkuimage");
                            File lokasibackupimage = new File(Environment.getExternalStorageDirectory(), "kasirkubackup/kasirkuimage");
                            try {
                                Oneforallfunc.copyfile(lokasidb, lokasibackupdb);
                                Oneforallfunc.copyfile(lokasiimage, lokasibackupimage);
                                Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, "Backup Berhasil", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    adb.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    adb.show();


                } else if (position == 5) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(com.dfit.dfpos.PengaturanActivity.this);
                    adb.setCancelable(false);
                    adb.setTitle("Informasi");
                    adb.setMessage("Pastikan folder kasirkubackup tidak terhapus di internal storage anda, jika tidak ada atau terhapus, " +
                            "anda bisa membuatnya dengan file explorer atau dengan melakukan backup pada pengaturan, " +
                            "dan pastikan data yang ingin anda restore berada didalam folder tersebut, jika datanya sudah dipindah ke Flashdisk, SDCARD atau sejenisnya" +
                            " maka copy dulu datanya ke dalam folder kasirkubackup sebelum melakukan restore");
                    adb.setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ActivityCompat.checkSelfPermission(com.dfit.dfpos.PengaturanActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(com.dfit.dfpos.PengaturanActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(com.dfit.dfpos.PengaturanActivity.this,
                                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                android.Manifest.permission.CAMERA}, 1);
                                return;
                            }

                            File lokasidb = getDatabasePath("kasirku.db");
                            File lokasibackupdb = new File(Environment.getExternalStorageDirectory(), "kasirkubackup/kasirku.db");
                            File lokasiimage = new File(getFilesDir(), "kasirkuimage");
                            File lokasibackupimage = new File(Environment.getExternalStorageDirectory(), "kasirkubackup/kasirkuimage");
                            try {
                                Oneforallfunc.copyfile(lokasibackupdb, lokasidb);
                                Oneforallfunc.copyfile(lokasibackupimage, lokasiimage);
                                Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, "Data Berhasil Di Restore", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                    adb.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    adb.show();

                } else if (position == 6) {
                    String pilihan[] = {"List", "Grid"};
                    AlertDialog.Builder adb = new AlertDialog.Builder(com.dfit.dfpos.PengaturanActivity.this);
                    adb.setTitle("Pilih Tipe Tampilan");
                    adb.setSingleChoiceItems(pilihan, sp.getInt("view_tipe",0), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                SQLiteDatabase db = dbo.getWritableDatabase();
                                db.beginTransaction();
                                try {
                                    db.execSQL("UPDATE pengaturan SET view_tipe=0 WHERE id=1");
                                    db.setTransactionSuccessful();
                                    Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, "Tampilan List", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                    db.close();
                                }
                                ed.putInt("view_tipe", 0);
                                ed.apply();
                            } else {
                                SQLiteDatabase db = dbo.getWritableDatabase();
                                db.beginTransaction();
                                try {
                                    db.execSQL("UPDATE pengaturan SET view_tipe=0 WHERE id=1");
                                    db.setTransactionSuccessful();
                                    Toast.makeText(com.dfit.dfpos.PengaturanActivity.this, "Tampilan Grid", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                    db.close();
                                }
                                ed.putInt("view_tipe", 1);
                                ed.apply();
                            }
                        }
                    });
                    adb.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    adb.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth diaktifkan", Toast.LENGTH_SHORT).show();
        }
    }
}
