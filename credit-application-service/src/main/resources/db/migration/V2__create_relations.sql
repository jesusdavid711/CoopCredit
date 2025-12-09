ALTER TABLE credit_applications
ADD CONSTRAINT fk_affiliate
FOREIGN KEY (affiliate_id) REFERENCES affiliates(id);

ALTER TABLE risk_evaluations
ADD CONSTRAINT fk_credit_application
FOREIGN KEY (credit_application_id) REFERENCES credit_applications(id);

CREATE INDEX idx_affiliate_document ON affiliates(document_number);
CREATE INDEX idx_application_affiliate ON credit_applications(affiliate_id);
CREATE INDEX idx_application_status ON credit_applications(status);
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_risk_eval_app ON risk_evaluations(credit_application_id);
