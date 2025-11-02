package br.com.ronna.control.services.Impl;

import br.com.ronna.control.dtos.*;
import br.com.ronna.control.models.FechamentoModel;
import br.com.ronna.control.models.ProdutoModel;
import br.com.ronna.control.models.VisitaModel;
import br.com.ronna.control.services.FaturaPdfService;
import br.com.ronna.control.services.VisitaService;
import lombok.var;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class FaturaPdfServiceImpl implements FaturaPdfService {

    private final SpringTemplateEngine templateEngine;
    private VisitaService visitaService;

    public FaturaPdfServiceImpl(VisitaService visitaService, SpringTemplateEngine templateEngine) {
        this.visitaService = visitaService;
        this.templateEngine = templateEngine;
    }


    @Override
    public byte[] gerarFaturaPdf(FechamentoResponseDto fechamentoNovoDto) throws IOException {
        // cabecalho e rodape em base64
        String cabecalhoBase64 = "";
        String rodapeBase64 = "";

        try(InputStream is = getClass().getResourceAsStream("/static/img/sux_cabecalho.png")) {
            if (is != null) {
                byte[] bytes = StreamUtils.copyToByteArray(is);
                cabecalhoBase64 = Base64.getEncoder().encodeToString(bytes);
            }
        }
        try(InputStream is = getClass().getResourceAsStream("/static/img/sux_rodape.png")) {
            if (is != null) {
                byte[] bytes = StreamUtils.copyToByteArray(is);
                rodapeBase64 = Base64.getEncoder().encodeToString(bytes);
            }
        }

        // Transiciona o obj para o dto de fatura
        var fatura = new FechamentoResponseFaturaDto();
        fatura.setFechamentoId(fechamentoNovoDto.getFechamentoId());
        fatura.setFechamentoInicio(fechamentoNovoDto.getFechamentoInicio());
        fatura.setFechamentoFinal(fechamentoNovoDto.getFechamentoFinal());
        fatura.setFechamentoValorServicos(fechamentoNovoDto.getFechamentoValorServicos());
        fatura.setFechamentoValorProdutos(fechamentoNovoDto.getFechamentoValorProdutos());
        fatura.setFechamentoStatus(fechamentoNovoDto.getFechamentoStatus());
        fatura.setCreatedDate(fechamentoNovoDto.getCreatedDate());
        fatura.setUpdatedDate(fechamentoNovoDto.getUpdatedDate());

        // Converter VisitaModel para VisitaFaturaDto
        fatura.setVisitas(new java.util.HashSet<>());
        for (var visitaModel : fechamentoNovoDto.getVisitas()) {
            if (fatura.getClienteNome() == null || fatura.getClienteNome().isEmpty()) {
                fatura.setClienteNome(visitaModel.getCliente().getClienteNome());
            }
            if (fatura.getClienteLocal() == null || fatura.getClienteLocal().isEmpty()) {
                fatura.setClienteLocal(visitaModel.getLocal().getLocalNome());
                if (visitaModel.getLocal().getLocalNome().equals("Padrão")) {
                    fatura.setClienteLocal(" ");
                }

            }
            var visitaFaturaDto = converterVisitaModelToDto(visitaModel);
            fatura.getVisitas().add(visitaFaturaDto);
        }

        System.out.println("Valor Produtos: " + fechamentoNovoDto.getFechamentoValorProdutos());
        System.out.println("Valor Servicos: " + fechamentoNovoDto.getFechamentoValorServicos());

        // Thymeleaf -> HTML
        Context ctx = new Context();
        ctx.setVariable("f", fatura);
        ctx.setVariable("cabecalho", cabecalhoBase64);
        ctx.setVariable("rodape", rodapeBase64);
        String html = templateEngine.process("fatura", ctx); //templates/fatura.html

        // Renderiza HTML -> PDF (A4)
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();


            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);
            renderer.finishPDF();
            return out.toByteArray();
        }


    }

    private VisitaFaturaDto converterVisitaModelToDto(VisitaModel visitaModel){
        var vDto = new VisitaFaturaDto();
        vDto.setVisitaInicio(visitaModel.getVisitaInicio());
        vDto.setVisitaFinal(visitaModel.getVisitaFinal());
        vDto.setCliente(visitaModel.getCliente().getClienteNome());
        vDto.setLocalCliente(visitaModel.getLocal().getLocalNome());
        vDto.setVisitaRemoto(visitaModel.isVisitaRemoto());

        // Funcionarios - concatenar nomes dos funcionarios separados por virgula
        var funcionariosNomes = new StringBuilder();
        for (var func : visitaModel.getFuncionarios()) {
            //Pega o nome do funcionario antes do primeiro espaço
            funcionariosNomes.append(func.getFuncionarioNome().split(" ")[0]).append(", ");
        }
        // Remove a ultima virgula e espaco
        if (funcionariosNomes.length() > 2) {
            funcionariosNomes.setLength(funcionariosNomes.length() - 2);
        }
        vDto.setFuncionarios(funcionariosNomes.toString());

        vDto.setVisitaDescricao(visitaModel.getVisitaDescricao());
        vDto.setVisitaValorProdutos(visitaModel.getVisitaValorProdutos());
        vDto.setVisitaTotalAbono(visitaModel.getVisitaTotalAbono());

        // Produtos
        // inicializa o set de produtos
        vDto.setProdutos(new java.util.HashSet<>());
        // chama o converter produtos para dto para cada produto dentro da visita
        var fakeProduto = new ProdutoFaturaDto();
        fakeProduto.setNome("Sem Produtos");
        fakeProduto.setQuantidade(0);
        fakeProduto.setVisitaInicio(visitaModel.getVisitaInicio());
        fakeProduto.setVisitaFinal(visitaModel.getVisitaFinal());
        fakeProduto.setPreco(0.0);

        for (var produto : visitaModel.getProdutos()) {
            if (produto.getNome() != null) {
                vDto.getProdutos().add(converterprodutoModelToDto(produto, visitaModel));
            }
        }
        return vDto;
    }


    private ProdutoFaturaDto converterprodutoModelToDto(ProdutoModel produtoModel, VisitaModel visitaModel) {
        var pDto = new ProdutoFaturaDto();
        pDto.setNome(produtoModel.getNome());
        pDto.setQuantidade(produtoModel.getQuantidade());
        pDto.setVisitaInicio(visitaModel.getVisitaInicio());
        pDto.setVisitaFinal(visitaModel.getVisitaFinal());
        pDto.setPreco(produtoModel.getPreco());
        return pDto;
    }
}
