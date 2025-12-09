package com.riskcentral.app.dto;

public class RiskEvaluationRequest {

    private String documento;
    private Double monto;
    private Integer plazo;

    public RiskEvaluationRequest() {
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
    }
}
