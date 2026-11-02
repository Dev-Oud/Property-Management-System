CREATE INDEX IF NOT EXISTS idx_companies_owner_id ON companies(owner_id);
CREATE INDEX IF NOT EXISTS idx_companies_status ON companies(status);
CREATE INDEX IF NOT EXISTS idx_companies_owner_status ON companies(owner_id, status);
CREATE INDEX IF NOT EXISTS idx_company_documents_company_id ON company_documents(company_id);
CREATE INDEX IF NOT EXISTS idx_company_documents_company_uploaded ON company_documents(company_id, uploaded_at);
CREATE INDEX IF NOT EXISTS idx_company_documents_uploaded_by ON company_documents(uploaded_by);
CREATE INDEX IF NOT EXISTS idx_company_documents_uploaded_at ON company_documents(uploaded_at);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
