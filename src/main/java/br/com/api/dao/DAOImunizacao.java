package br.com.api.dao;

import br.com.api.model.Imunizacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DAOImunizacao {
    public static Connection conexao = null;

    public static int inserir(Imunizacao imunizacao) throws SQLException {
        String sql = "INSERT INTO imunizacoes (id_paciente, id_dose, data_aplicacao, fabricante, lote, local_aplicacao, profissional_aplicador) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            comando.setInt(1, imunizacao.getIdPaciente());
            comando.setInt(2, imunizacao.getIdDose());
            comando.setDate(3, imunizacao.getDataAplicacao());
            comando.setString(4, imunizacao.getFabricante());
            comando.setString(5, imunizacao.getLote());
            comando.setString(6, imunizacao.getLocalAplicacao());
            comando.setString(7, imunizacao.getProfissionalAplicador());
            comando.executeUpdate();
            try (ResultSet idGerado = comando.getGeneratedKeys()) {
                if (idGerado.next()) {
                    return idGerado.getInt(1);
                }
            }
        }
        throw new SQLException("Erro ao inserir imunização: nenhum ID gerado.");
    }

    public static HashMap<String, Object> consultarPorID(int id) throws SQLException {
        HashMap<String, Object> imunizacao = null;

        String sql = """
                    SELECT
                        imunizacoes.id,
                        paciente.nome AS paciente,
                        vacina.vacina,
                        dose.dose,
                        imunizacoes.data_aplicacao,
                        imunizacoes.fabricante,
                        imunizacoes.lote,
                        imunizacoes.local_aplicacao,
                        imunizacoes.profissional_aplicador
                    FROM imunizacoes
                    JOIN paciente ON imunizacoes.id_paciente = paciente.id
                    JOIN dose ON imunizacoes.id_dose = dose.id
                    JOIN vacina ON dose.id_vacina = vacina.id
                    WHERE imunizacoes.id = ?
                """;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    imunizacao = new HashMap<>();
                    imunizacao.put("id", resultado.getInt("id"));
                    imunizacao.put("Nome do Paciente", resultado.getString("paciente"));
                    imunizacao.put("Vacina", resultado.getString("vacina"));
                    imunizacao.put("Dose", resultado.getString("dose"));
                    imunizacao.put("Data da Aplicação", resultado.getDate("data_aplicacao"));
                    imunizacao.put("Fabricante", resultado.getString("fabricante"));
                    imunizacao.put("Lote", resultado.getString("lote"));
                    imunizacao.put("Local", resultado.getString("local_aplicacao"));
                    imunizacao.put("Profissional", resultado.getString("profissional_aplicador"));
                }
            }
        }

        return imunizacao;
    }

    public static ArrayList<HashMap<String, Object>> consultarTodasImunizacoes() throws SQLException {
        ArrayList<HashMap<String, Object>> lista = new ArrayList<>();

        String sql = """
                    SELECT
                        imunizacoes.id,
                        paciente.nome AS paciente,
                        vacina.vacina,
                        dose.dose,
                        imunizacoes.data_aplicacao,
                        imunizacoes.fabricante,
                        imunizacoes.lote,
                        imunizacoes.local_aplicacao,
                        imunizacoes.profissional_aplicador
                    FROM imunizacoes
                    JOIN paciente ON imunizacoes.id_paciente = paciente.id
                    JOIN dose ON imunizacoes.id_dose = dose.id
                    JOIN vacina ON dose.id_vacina = vacina.id
                """;

        try (Statement comando = conexao.createStatement();
                ResultSet resultado = comando.executeQuery(sql)) {

            while (resultado.next()) {
                HashMap<String, Object> imunizacao = new HashMap<>();
                imunizacao.put("id", resultado.getInt("id"));
                imunizacao.put("Nome do Paciente", resultado.getString("paciente"));
                imunizacao.put("Vacina", resultado.getString("vacina"));
                imunizacao.put("Dose", resultado.getString("dose"));
                imunizacao.put("Data da Aplicação", resultado.getDate("data_aplicacao"));
                imunizacao.put("Fabricante", resultado.getString("fabricante"));
                imunizacao.put("Lote", resultado.getString("lote"));
                imunizacao.put("Local", resultado.getString("local_aplicacao"));
                imunizacao.put("Profissional", resultado.getString("profissional_aplicador"));

                lista.add(imunizacao);
            }
        }

        return lista;
    }

    public static ArrayList<HashMap<String, Object>> consultarImunizacoesPorPaciente(int idPaciente) throws SQLException {
        ArrayList<HashMap<String, Object>> lista = new ArrayList<>();
        
        String sql = """
            SELECT 
                imunizacoes.id, 
                paciente.nome AS "Nome do Paciente", 
                vacina.vacina AS Vacina, 
                dose.dose AS Dose, 
                imunizacoes.data_aplicacao AS "Data da Aplicação", 
                imunizacoes.fabricante AS Fabricante, 
                imunizacoes.lote AS Lote, 
                imunizacoes.local_aplicacao AS Local, 
                imunizacoes.profissional_aplicador AS Profissional
            FROM imunizacoes
            JOIN paciente ON imunizacoes.id_paciente = paciente.id
            JOIN dose ON imunizacoes.id_dose = dose.id
            JOIN vacina ON dose.id_vacina = vacina.id
            WHERE imunizacoes.id_paciente = ?
        """;
        
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idPaciente);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    HashMap<String, Object> imunizacao = new HashMap<>();
                    imunizacao.put("ID", resultado.getInt("id"));
                    imunizacao.put("Nome do Paciente", resultado.getString("Nome do Paciente"));
                    imunizacao.put("Vacina", resultado.getString("Vacina"));
                    imunizacao.put("Dose", resultado.getString("Dose"));
                    imunizacao.put("Data da Aplicação", resultado.getDate("Data da Aplicação"));
                    imunizacao.put("Fabricante", resultado.getString("Fabricante"));
                    imunizacao.put("Lote", resultado.getString("Lote"));
                    imunizacao.put("Local", resultado.getString("Local"));
                    imunizacao.put("Profissional", resultado.getString("Profissional"));
                    
                    lista.add(imunizacao);
                }
            }
        }
        
        return lista;
    }
    

    public static int atualizarImunizacao(Imunizacao imunizacao) throws SQLException {
        String sql = "UPDATE imunizacoes SET id_paciente = ?, id_dose = ?, data_aplicacao = ?, fabricante = ?, lote = ?, local_aplicacao = ?, profissional_aplicador = ? WHERE id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, imunizacao.getIdPaciente());
            comando.setInt(2, imunizacao.getIdDose());
            comando.setDate(3, imunizacao.getDataAplicacao());
            comando.setString(4, imunizacao.getFabricante());
            comando.setString(5, imunizacao.getLote());
            comando.setString(6, imunizacao.getLocalAplicacao());
            comando.setString(7, imunizacao.getProfissionalAplicador());
            comando.setInt(8, imunizacao.getId());
            return comando.executeUpdate();
        }
    }

    public static int excluirPorID(int id) throws SQLException {
        String sql = "DELETE FROM imunizacoes WHERE id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate();
        }
    }

    public static int excluirPorIdPaciente(int idPaciente) throws SQLException {
        String sql = "DELETE FROM imunizacoes WHERE id_paciente = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idPaciente);
            return comando.executeUpdate();
        }
    }

    public static ArrayList<HashMap<String, Object>> consultarPorIdPacienteEIntervalo(int idPaciente, Date dtInicio, Date dtFim) throws SQLException {
        ArrayList<HashMap<String, Object>> lista = new ArrayList<>();
        
        String sql = """
            SELECT 
                imunizacoes.id, 
                paciente.nome AS "Nome do Paciente", 
                vacina.vacina AS Vacina, 
                dose.dose AS Dose, 
                imunizacoes.data_aplicacao AS "Data da Aplicação", 
                imunizacoes.fabricante AS Fabricante, 
                imunizacoes.lote AS Lote, 
                imunizacoes.local_aplicacao AS Local, 
                imunizacoes.profissional_aplicador AS Profissional
            FROM imunizacoes
            JOIN paciente ON imunizacoes.id_paciente = paciente.id
            JOIN dose ON imunizacoes.id_dose = dose.id
            JOIN vacina ON dose.id_vacina = vacina.id
            WHERE imunizacoes.id_paciente = ?
              AND imunizacoes.data_aplicacao BETWEEN ? AND ?
        """;
        
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idPaciente);
            comando.setDate(2, dtInicio);
            comando.setDate(3, dtFim);
            
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    HashMap<String, Object> imunizacao = new HashMap<>();
                    imunizacao.put("ID", resultado.getInt("id"));
                    imunizacao.put("Nome do Paciente", resultado.getString("Nome do Paciente"));
                    imunizacao.put("Vacina", resultado.getString("Vacina"));
                    imunizacao.put("Dose", resultado.getString("Dose"));
                    imunizacao.put("Data da Aplicação", resultado.getDate("Data da Aplicação"));
                    imunizacao.put("Fabricante", resultado.getString("Fabricante"));
                    imunizacao.put("Lote", resultado.getString("Lote"));
                    imunizacao.put("Local", resultado.getString("Local"));
                    imunizacao.put("Profissional", resultado.getString("Profissional"));
                    
                    lista.add(imunizacao);
                }
            }
        }
        
        return lista;
    }
    
}
