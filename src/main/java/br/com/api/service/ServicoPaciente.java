package br.com.api.service;

import java.sql.Date;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.api.dao.DAOPaciente;
import br.com.api.model.Paciente;
import spark.Request;
import spark.Response;
import spark.Route;

public class ServicoPaciente {
    // Método para lidar com a rota de adicionar paciente
    public static Route cadastrarPaciente() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                // Extrai os parâmetros do corpo da requisição HTTP
                String nome = request.queryParams("nome");
                String cpf = request.queryParams("cpf");
                String sexo = request.queryParams("sexo");
                Date dataNascimento = Date.valueOf(request.queryParams("data_nascimento"));

                // Cria o objeto paciente
                Paciente paciente = new Paciente(0, nome, cpf, sexo, dataNascimento);

                try {
                    // Passa o objeto para o DAO realizar a inserção no banco de dados
                    // e recebe o  id gerado no banco de dados
                    int idPaciente = DAOPaciente.inserir(paciente);

                    // Define o status code do HTTP
                    response.status(201); // 201 Created

                    // Retorna o id criado e retorna via HTTP response
                    return "{\"message\": \"Paciente criado com o ID " + idPaciente + " com sucesso.\"}";

                } catch (Exception e) {
                    response.status(500); // 500 Erro no servidor
                    // Retorna a exceção gerada pelo DAOPaciente caso exista
                    return "{\"message\": \"Erro ao criar paciente. " + e.getMessage() + "\"}";
                }
            }
        };
    }

    // Método para lidar com a rota de buscar paciente por ID
    public static Route consultarPacientePorId() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                // Classe para converter objeto para JSON
                ObjectMapper converteJson = new ObjectMapper();

                int id;

                try {
                    // Extrai o parâmetro id da URL (header HTTP), e converte para inteiro
                    id = Integer.parseInt(request.params(":id"));

                    // Busca o paciente no banco de dados pela id
                    Paciente paciente = DAOPaciente.consultarPorID(id);

                    if (paciente != null) {
                        // Define o HTTP status code
                        response.status(200); // 200 OK

                        // Retorna o objeto encontrado no formato JSON
                        return converteJson.writeValueAsString(paciente);
                    } else {
                        // Define o HTTP status code
                        response.status(209); // 209 Consulta realizada com sucesso mas não tem nenhum registro no banco
                        return "{\"message\": \"Nenhum paciente encontrado com este ID.\"}";
                    }
                } catch (NumberFormatException e) {
                    // Define o HTTP status code
                    response.status(400); // 400 Requisição incorreta, foi fornecido um id que não pode ser convertido para inteiro
                    return "{\"message\": \"ID fornecido está no formato incorreto.\"}";
                }
            }
        };
    }

    // Método para lidar com a rota de buscar todos os pacientes
    public static Route consultarTodosPacientes() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                ObjectMapper converteJson = new ObjectMapper();

                // Busca todos os pacientes cadastrados no banco de dados
                ArrayList<Paciente> pacientes = DAOPaciente.consultarTodosPacientes();

                // Se o arraylist estiver vazio
                if (pacientes.isEmpty()) {
                    response.status(200); // 209
                    return "{\"message\": \"Nenhum paciente encontrado no banco de dados.\"}";
                } else {
                    // Se não estiver vazio devolve o arraylist convertido para JSON
                    response.status(200); // 200 Ok
                    return converteJson.writeValueAsString(pacientes);
                }
            }
        };
    }

    // Método para lidar com a rota de atualizar paciente
    public static Route alterarPaciente() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    // Extrai os parâmetros do corpo da requisição HTTP
                    int id = Integer.parseInt(request.params(":id"));
                    String nome = request.queryParams("nome");
                    String cpf = request.queryParams("cpf");
                    String sexo = request.queryParams("sexo");
                    Date dataNascimento = Date.valueOf(request.queryParams("data_nascimento"));

                    // Cria o objeto paciente na memória
                    Paciente paciente = new Paciente(id, nome, cpf, sexo, dataNascimento);

                    // Envia o objeto para ser atualizado no banco de dados pelo DAO
                    // e armazena a quantidade de linhas alteradas
                    int qtdeLinhasAlteradas = DAOPaciente.atualizarPaciente(paciente);

                    // Se a quantidade de linhas alteradas for maior que 0 significa que existia o paciente no banco de dados
                    if (qtdeLinhasAlteradas > 0) {
                        response.status(200); // 200 Ok
                        return "{\"message\": \"Paciente com id " + id + " foi atualizado com sucesso.\"}";
                    // Se não for maior que 0 não existia o paciente no banco de dados
                    } else {
                        response.status(209); // 404 Not Found
                        return "{\"message\": \"O paciente com id " + id + " não foi encontrado.\"}";
                    }

                } catch (NumberFormatException e) { // Algum erro de conversão do id passado na URL
                    response.status(400);
                    return "{\"message\": \"ID fornecido está no formato incorreto.\"}";
                } catch (Exception e) {
                    response.status(500);
                    return "{\"message\": \"Erro ao processar a requisição.\"}";
                }
            }
        };
    }

    // Método para lidar com a rota de excluir paciente
    public static Route excluirPaciente() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    // Extrai o parâmetro id da URL (header HTTP), e converte para inteiro
                    int id = Integer.parseInt(request.params(":id"));

                    // Envia o id a ser excluído para o DAO e recebe a quantidade de linhas excluídas
                    int linhasExcluidas = DAOPaciente.excluirPorID(id);

                    // Se a quantidade de linhas for maior que 0 significa que o paciente existia no banco de dados
                    if (linhasExcluidas > 0) {
                        response.status(200); // Exclusão com sucesso
                        return "{\"message\": \"Paciente com id " + id + " foi excluído com sucesso.\"}";
                    // Se não for maior que 0 o paciente não existia no banco de dados
                    } else {
                        response.status(209); // Id não encontrado
                        return "{\"message\": \"Paciente com id " + id + " não foi encontrado no banco de dados.\"}";
                    }
                } catch (NumberFormatException e) { // Alguma exceção na conversão do id fornecido na URL
                    response.status(400);
                    return "{\"message\": \"ID fornecido está no formato incorreto.\"}";
                }
            }
        };
    }
}
