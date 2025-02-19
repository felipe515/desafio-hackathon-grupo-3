package br.com.api.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.api.dao.DAOImunizacao;
import br.com.api.model.Imunizacao;
import spark.Request;
import spark.Response;
import spark.Route;

public class ServicoImunizacao {
    public static Route cadastrarImunizacao() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                int idPaciente = Integer.parseInt(request.queryParams("id_paciente"));
                int idDose = Integer.parseInt(request.queryParams("id_dose"));
                Date dataAplicacao = Date.valueOf(request.queryParams("data_aplicacao"));
                String fabricante = request.queryParams("fabricante");
                String lote = request.queryParams("lote");
                String localAplicacao = request.queryParams("local_aplicacao");
                String profissionalAplicador = request.queryParams("profissional_aplicador");

                Imunizacao imunizacao = new Imunizacao(0, idPaciente, idDose, dataAplicacao, fabricante, lote, localAplicacao, profissionalAplicador);

                try {
                    int idImunizacao = DAOImunizacao.inserir(imunizacao);
                    response.status(201);
                    return "{\"message\": \"Imunização criada com o ID " + idImunizacao + " com sucesso.\"}";
                } catch (SQLException e) {
                    response.status(500);
                    return "{\"message\": \"Erro ao cadastrar imunização: " + e.getMessage() + "\"}";
                }
            }
        };
    }

    public static Route consultarImunizacaoPorId() {
        return (Request request, Response response) -> {
            ObjectMapper converteJson = new ObjectMapper();
            int id = Integer.parseInt(request.params(":id"));
    
            HashMap<String, Object> imunizacao = DAOImunizacao.consultarPorID(id);
    
            if (imunizacao != null) {
                response.status(200);
                return converteJson.writeValueAsString(imunizacao);
            } else {
                response.status(404);
                return "{\"message\": \"Imunização não encontrada.\"}";
            }
        };
    }
    

    public static Route consultarTodasImunizacoes() {
    return (Request request, Response response) -> {
        ObjectMapper converteJson = new ObjectMapper();
        try {
            ArrayList<HashMap<String, Object>> imunizacoes = DAOImunizacao.consultarTodasImunizacoes();
            if (imunizacoes.isEmpty()) {
                response.status(200);
                return "{\"message\": \"Nenhuma imunização encontrada.\"}";
            } else {
                response.status(200);
                return converteJson.writeValueAsString(imunizacoes);
            }
        } catch (SQLException e) {
            response.status(500);
            return "{\"message\": \"Erro ao buscar imunizações: " + e.getMessage() + "\"}";
        }
    };
}

    public static Route consultarImunizacoesPorIdPaciente() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                ObjectMapper converteJson = new ObjectMapper();
                int idPaciente = Integer.parseInt(request.params(":id"));
                
                ArrayList<HashMap<String, Object>> imunizacoes = DAOImunizacao.consultarImunizacoesPorPaciente(idPaciente);
                
                if (imunizacoes.isEmpty()) {
                    response.status(404);
                    return "{\"message\": \"Nenhuma imunização encontrada para este paciente.\"}";
                } else {
                    response.status(200);
                    return converteJson.writeValueAsString(imunizacoes);
                }
            }
        };
    }
    

    public static Route alterarImunizacao() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                int id = Integer.parseInt(request.params(":id"));
                int idPaciente = Integer.parseInt(request.queryParams("id_paciente"));
                int idDose = Integer.parseInt(request.queryParams("id_dose"));
                Date dataAplicacao = Date.valueOf(request.queryParams("data_aplicacao"));
                String fabricante = request.queryParams("fabricante");
                String lote = request.queryParams("lote");
                String localAplicacao = request.queryParams("local_aplicacao");
                String profissionalAplicador = request.queryParams("profissional_aplicador");

                Imunizacao imunizacao = new Imunizacao(id, idPaciente, idDose, dataAplicacao, fabricante, lote, localAplicacao, profissionalAplicador);

                try {
                    int qtdeLinhasAlteradas = DAOImunizacao.atualizarImunizacao(imunizacao);
                    if (qtdeLinhasAlteradas > 0) {
                        response.status(200);
                        return "{\"message\": \"Imunização com id " + id + " foi atualizada com sucesso.\"}";
                    } else {
                        response.status(404);
                        return "{\"message\": \"Imunização com id " + id + " não foi encontrada.\"}";
                    }
                } catch (SQLException e) {
                    response.status(500);
                    return "{\"message\": \"Erro ao atualizar imunização: " + e.getMessage() + "\"}";
                }
            }
        };
    }

    public static Route excluirImunizacao() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                int id = Integer.parseInt(request.params(":id"));
                try {
                    int qtdeLinhasExcluidas = DAOImunizacao.excluirPorID(id);
                    if (qtdeLinhasExcluidas > 0) {
                        response.status(200);
                        return "{\"message\": \"Imunização com id " + id + " foi excluída com sucesso.\"}";
                    } else {
                        response.status(404);
                        return "{\"message\": \"Imunização com id " + id + " não foi encontrada.\"}";
                    }
                } catch (SQLException e) {
                    response.status(500);
                    return "{\"message\": \"Erro ao excluir imunização: " + e.getMessage() + "\"}";
                }
            }
        };
    }

    public static Route excluirImunizacoesPorIdPaciente() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                int idPaciente = Integer.parseInt(request.params(":id"));
                try {
                    int qtdeLinhasExcluidas = DAOImunizacao.excluirPorIdPaciente(idPaciente);
                    if (qtdeLinhasExcluidas > 0) {
                        response.status(200);
                        return "{\"message\": \"Todas as imunizações do paciente com id " + idPaciente + " foram excluídas com sucesso.\"}";
                    } else {
                        response.status(404);
                        return "{\"message\": \"Nenhuma imunização encontrada para o paciente com id " + idPaciente + ".\"}";
                    }
                } catch (SQLException e) {
                    response.status(500);
                    return "{\"message\": \"Erro ao excluir imunizações do paciente: " + e.getMessage() + "\"}";
                }
            }
        };
    }

    public static Route consultarImunizacoesPorIdPacienteEIntervalo() {
        return new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                ObjectMapper converteJson = new ObjectMapper();
                int idPaciente = Integer.parseInt(request.params(":id"));
                Date dtInicio = Date.valueOf(request.params(":dt_ini"));
                Date dtFim = Date.valueOf(request.params(":dt_fim"));
                
                ArrayList<HashMap<String, Object>> imunizacoes = DAOImunizacao.consultarPorIdPacienteEIntervalo(idPaciente, dtInicio, dtFim);
                
                if (imunizacoes.isEmpty()) {
                    response.status(404);
                    return "{\"message\": \"Nenhuma imunização encontrada para este paciente no período especificado.\"}";
                } else {
                    response.status(200);
                    return converteJson.writeValueAsString(imunizacoes);
                }
            }
        };
    }
    
}