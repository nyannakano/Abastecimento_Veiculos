package com.gabrielnakano.abastecimento_veiculos

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var etCod: EditText
    private lateinit var etCidade: EditText
    private lateinit var etQuantidadeLitros: EditText
    private lateinit var spDescricao: Spinner

    private lateinit var banco: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCod = findViewById(R.id.etCod)
        etCidade = findViewById(R.id.etCidade)
        etQuantidadeLitros = findViewById(R.id.etQuantidadeLitros)
        spDescricao = findViewById(R.id.spDescricao)
        val options = arrayOf("1 - Etanol", "2 - Gasolina", "3 - Diesel", "4 - Gás Natural")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDescricao.adapter = adapter

        banco =
            SQLiteDatabase.openOrCreateDatabase(this.getDatabasePath("dbfileabasteci.sqlite"), null)

        banco.execSQL("CREATE TABLE IF NOT EXISTS abastecimento (_id INTEGER PRIMARY KEY AUTOINCREMENT, cidade VARCHAR, descricao VARCHAR, litros INT)")

    }

    fun btIncluirOnClick(view: View) {
        var registro = ContentValues()
        registro.put("cidade", etCidade.text.toString())
        registro.put("descricao", spDescricao.selectedItem.toString())
        registro.put("litros", etQuantidadeLitros.text.toString())

        banco.insert("abastecimento", null, registro)

        Toast.makeText(this, "Registro criado com sucesso!", Toast.LENGTH_SHORT).show()
    }

    fun btPesquisarOnClick(view: View) {
        val cod = etCod.text.toString()

        val registros = banco
            .query(
                "abastecimento",
                null,
                "_id=${cod}",
                null,
                null,
                null,
                null,
                null
            )

        if (registros.moveToNext()) {
            etCidade.setText(registros.getString(1))
            etQuantidadeLitros.setText(registros.getString(3))
            val selectedValue = registros.getString(2)
            val adapter = spDescricao.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(selectedValue)
            spDescricao.setSelection(position)
        } else {
            Toast.makeText(
                this,
                "Não foi encontrado nenhum registro com este id.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun btDadosEstatisticosOnClick(view: View) {
        val etanolRegistros = banco.rawQuery(
            "SELECT SUM(litros) FROM abastecimento WHERE descricao = ?",
            arrayOf("1 - Etanol")
        )
        val gasolinaRegistros = banco.rawQuery(
            "SELECT SUM(litros) FROM abastecimento WHERE descricao = ?",
            arrayOf("2 - Gasolina")
        )
        val dieselRegistros = banco.rawQuery(
            "SELECT SUM(litros) FROM abastecimento WHERE descricao = ?",
            arrayOf("3 - Diesel")
        )
        val gasNaturalRegistros = banco.rawQuery(
            "SELECT SUM(litros) FROM abastecimento WHERE descricao = ?",
            arrayOf("4 - Gás Natural")
        )

        if (etanolRegistros.moveToNext() && gasolinaRegistros.moveToNext() && dieselRegistros.moveToNext() && gasNaturalRegistros.moveToNext()) {
            val resultEtanol = etanolRegistros.getFloat(0)
            val resultGasolina = gasolinaRegistros.getFloat(0)
            val resultDiesel = dieselRegistros.getFloat(0)
            val resultGasNatural = gasNaturalRegistros.getFloat(0)

            var saida = StringBuilder()

            saida.append("Etanol: ")
            saida.append(resultEtanol)
            saida.append(" Gasolina: ")
            saida.append(resultGasolina)
            saida.append(" Diesel: ")
            saida.append(resultDiesel)
            saida.append(" Gás Natural: ")
            saida.append(resultGasNatural)
            saida.append("\n")


            Toast.makeText(this, saida.toString(), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
        }

    }

}