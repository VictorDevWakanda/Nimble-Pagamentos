CREATE TABLE cobranca (
    id_cobranca UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario_originador UUID NOT NULL,
    cpf_destinatario VARCHAR(14) NOT NULL,
    valor_cobranca DECIMAL(10, 2) NOT NULL CHECK (valor_cobranca > 0),
    descricao VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE' CHECK (status IN ('PENDENTE', 'PAGA', 'CANCELADA')),
    data_expiracao TIMESTAMP,
    data_pagamento TIMESTAMP,
    data_cancelamento TIMESTAMP,
    forma_pagamento VARCHAR(20) CHECK (forma_pagamento IN ('SALDO', 'CARTAO')),

    CONSTRAINT fk_cobranca_usuario_originador
        FOREIGN KEY (id_usuario_originador) REFERENCES usuario(id_usuario)
);