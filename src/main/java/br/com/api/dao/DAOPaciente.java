package br.com.api.dao;

import br.com.api.model.Paciente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOPaciente {
    // Atributo utilizado para receber a conexão criada no método main
    public static Connection conexao = null;

    
    public static int inserir(Paciente paciente) throws SQLException {
        
        String sql = "INSERT INTO paciente (nome, cpf, sexo, data_nascimento) VALUES (?, ?, ?, ?)";

        // Statement.RETURN_GENERATED_KEYS parâmetro que diz que o banco de dados deve retornar o id da chave primária criada
        try (PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Adiciona os valores de nome, cpf, sexo e data_nascimento no lugar das ? da string SQL
            comando.setString(1, paciente.getNome());
            comando.setString(2, paciente.getCpf());
            comando.setString(3, paciente.getSexo());
            comando.setDate(4, paciente.getDataNascimento());

            // Envia o SQL para o banco de dados
            comando.executeUpdate();

           
            try (ResultSet idGerado = comando.getGeneratedKeys()) {
                
                if (idGerado.next()) {
                    
                    return idGerado.getInt(1);
                }
            }
        }

        // Caso o fluxo de execução chegue até este ponto é porque ocorreu algum erro
        // Gera uma exceção de negócio dizendo que nenhum id foi gerado
        throw new SQLException("Erro ao inserir paciente: nenhum ID gerado.");
    }

    
    public static ArrayList<Paciente> consultarTodosPacientes() throws SQLException {
        // Cria o array list para receber os dados dos pacientes que retornarão do banco de dados
        ArrayList<Paciente> lista = new ArrayList<>();

        
        String sql = "SELECT * FROM paciente";

        try (Statement comando = conexao.createStatement(); // Cria o comando
             ResultSet resultado = comando.executeQuery(sql); // Executa a consulta
        ) {
            // Para cada registro retornado do banco de dados
            while (resultado.next()) {
                // Cria um novo objeto paciente
                Paciente novoPaciente = new Paciente(
                    resultado.getInt("id"),
                    resultado.getString("nome"),
                    resultado.getString("cpf"),
                    resultado.getString("sexo"),
                    resultado.getDate("data_nascimento")
                );

                // Adiciona o objeto paciente no array list
                lista.add(novoPaciente);
            }
        }

        // Retorna o array list de objetos pacientes
        return lista;
    }

   
    public static Paciente consultarPorID(int id) throws SQLException {
        // Inicia o objeto paciente como null
        Paciente paciente = null;

        // Define o SQL da consulta
        String sql = "SELECT * FROM paciente WHERE id = ?";

        try (PreparedStatement comando = conexao.prepareStatement(sql)) { // Cria o comando para receber SQL dinâmico
            // Substitui a ? pelo código do paciente
            comando.setInt(1, id);

            // Executa o comando SQL
            ResultSet resultado = comando.executeQuery();

            // Verifica se tem algum resultado retornado pelo banco
            if (resultado.next()) {
                // Cria o objeto paciente com os dados retornados do banco
                paciente = new Paciente(
                    resultado.getInt("id"),
                    resultado.getString("nome"),
                    resultado.getString("cpf"),
                    resultado.getString("sexo"),
                    resultado.getDate("data_nascimento")
                );
            }

          
            return paciente;
        }
    }

  
    public static int excluirPorID(int id) throws SQLException {
        // Define o SQL de exclusão
        String sql = "DELETE FROM paciente WHERE id = ?";

        try (PreparedStatement comando = conexao.prepareStatement(sql)) { // Cria a conexão com o SQL a ser preparado
            // Substitui a ? pelo id do paciente
            comando.setInt(1, id);

            // Executa a consulta e armazena o resultado da quantidade de linhas excluídas na variável
            int qtdeLinhasExcluidas = comando.executeUpdate();

            return qtdeLinhasExcluidas;
        } catch (Exception e) {
            throw e;
        }
    }

  
    public static int atualizarPaciente(Paciente paciente) throws SQLException {
        // Define o SQL
        String sql = "UPDATE paciente SET nome = ?, cpf = ?, sexo = ?, data_nascimento = ? WHERE id = ?";

        try (PreparedStatement comando = conexao.prepareStatement(sql)) { // Cria a conexão para receber o SQL dinâmico
            // Substitui as ? pelos valores de nome, cpf, sexo, data_nascimento e id
            comando.setString(1, paciente.getNome());
            comando.setString(2, paciente.getCpf());
            comando.setString(3, paciente.getSexo());
            comando.setDate(4, paciente.getDataNascimento());
            comando.setInt(5, paciente.getId());

            // Executa a atualização e armazena o retorno do banco com a quantidade de linhas atualizadas
            int qtdeLinhasAlteradas = comando.executeUpdate();

            // Retorna a quantidade de linhas atualizadas
            return qtdeLinhasAlteradas;
        }
    }
}
