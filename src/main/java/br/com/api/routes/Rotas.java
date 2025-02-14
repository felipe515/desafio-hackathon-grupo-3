package br.com.api.routes;

import br.com.api.service.ServicoUsuario;
import br.com.api.service.ServicoPaciente;
import spark.Spark;

public class Rotas {
    
    public static void processarRotas(){
        //cadastra no spark quais rotas existem e quais metodos 
        //devem ser executados quando cada rota for requisitada
        Spark.post("/cadastrar", ServicoUsuario.cadastrarUsuario());
        Spark.get("/consultar/:id", ServicoUsuario.consultarUsuarioPorId());
        Spark.get("/consultar", ServicoUsuario.consultarTodosUsuarios());
        Spark.put("/alterar/:id", ServicoUsuario.alterarUsuario());
        Spark.delete("/excluir/:id", ServicoUsuario.excluirUsuario());

        // Rotas de paciente
        Spark.post("/paciente/inserir", ServicoPaciente.cadastrarPaciente());
        Spark.get("/paciente/consultar/:id", ServicoPaciente.consultarPacientePorId());
        Spark.get("/paciente/consultar", ServicoPaciente.consultarTodosPacientes());
        Spark.put("/paciente/alterar/:id", ServicoPaciente.alterarPaciente());
        Spark.delete("/paciente/excluir/:id", ServicoPaciente.excluirPaciente());
        
        //TO DO: Para criar novas rotas, basta adicionar novas linhas seguindo o padrao abaixo, 
        //onde XXXX e o metodo http (post, get, put ou delete), yyyyyy a url que define a rota
        //e ZZZZZ o metodo a ser executado quando a rota for acionada
        //Spark.XXXXX("YYYYYYY", ZZZZZ);
    }
    
    //Para criar novos metodos basta utilizar o esqueleto abaixo
    //Metodo Esqueleto
    //XXXX: Nome do metodo que o usuario quer criar
    //YYYY: Parametros de entrada para o metodo se existirem
    //ZZZZ: Implementação do método
    //QQQQ: Status code do HTTP
    //SSSS: Informação que será retornado
    //
    // public static Route XXXX(YYYY) {
    //     return new Route() {
    //         @Override
    //         public Object handle(Request request, Response response) throws Exception {
    //
    //             ZZZZ
    //
    //             response.status(QQQQ); 
    //             return SSSS;
    //         }
    //     };
    // }
   
}
