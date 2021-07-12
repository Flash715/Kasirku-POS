package com.ivan.kasirku;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    EditText ednama_usaha,edalamat,ednohp,edemail,edwebsite,edusername,edpassword,edrepassword;
    Button bsimpan;
    Dblocalhelper dbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ednama_usaha=findViewById(R.id.ednama_usaha);
        edalamat=findViewById(R.id.edalamat);
        ednohp=findViewById(R.id.ednohp);
        edemail=findViewById(R.id.edemail);
        edwebsite=findViewById(R.id.edwebsite);
        edusername=findViewById(R.id.edusername);
        edpassword=findViewById(R.id.edpassword);
        edrepassword=findViewById(R.id.edpassword);
        bsimpan=findViewById(R.id.bsimpan);
        dbo=new Dblocalhelper(this);
        simpan();
    }

    private void simpan(){
        bsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edpassword.getText().toString().equals(edrepassword.getText().toString())) {
                    SQLiteDatabase db = dbo.getWritableDatabase();
                    db.beginTransaction();
                    try {
                        String currenttime = new SimpleDateFormat("yyyyMMddHHmmssSSSSSS").format(new Date());
                        String nama_usaha = ednama_usaha.getText().toString();
                        String alamat = edalamat.getText().toString();
                        String nohp = ednohp.getText().toString();
                        String email = edemail.getText().toString();
                        String website = edwebsite.getText().toString();
                        String username = edusername.getText().toString();
                        String password = edpassword.getText().toString();
                        db.execSQL("UPDATE perusahaan SET nama_usaha='" + nama_usaha + "', alamat_usaha='" + alamat + "'," +
                                "nohp_usaha='" + nohp + "',email_usaha='" + email + "',website='" + website + "',date_created='"+currenttime+"' WHERE id=1");
                        db.execSQL("INSERT INTO pengguna(kode_user,email,username,password,read_persediaan,write_persediaan, " +
                                "read_pembelian,write_pembelian,read_penjualan,write_penjualan,read_laporan,read_user)" +
                                "VALUES('1001','"+email+"','"+username+"','"+password+"',1,1,1,1,1,1,1,1)");
                        db.setTransactionSuccessful();
                        AlertDialog.Builder adb = new AlertDialog.Builder(RegisterActivity.this);
                        adb.setTitle("Informasi");
                        adb.setMessage("Register Berhasil, Anda sudah bisa masuk ke aplikasi dengan username dan password yang sudah anda daftarkan");
                        adb.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        adb.show();
                    } catch (Exception ex) {
                        deleteDatabase("kasirku.db");
                        AlertDialog.Builder adb=new AlertDialog.Builder(RegisterActivity.this);
                        adb.setTitle("Informasi");
                        adb.setMessage(ex.getMessage());
                        adb.show();
                    } finally {
                        db.endTransaction();
                        db.close();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Proses Gagal, Password tidak cocok", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
