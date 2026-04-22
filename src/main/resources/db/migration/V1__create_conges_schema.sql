CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE conventions (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                   VARCHAR(150) NOT NULL,
    override_annual_days   INT,
    min_notice_days        INT,
    eligibility_conditions TEXT
);

CREATE TABLE leave_types (
    id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name                    VARCHAR(100) NOT NULL,
    code                    VARCHAR(30)  NOT NULL UNIQUE,
    default_annual_balance  NUMERIC(5,2) NOT NULL,
    requires_justification  BOOLEAN      NOT NULL DEFAULT FALSE,
    is_carry_over_allowed   BOOLEAN      NOT NULL DEFAULT FALSE,
    convention_id           UUID         REFERENCES conventions(id)
);

CREATE TABLE leave_parameters (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    calculation_mode VARCHAR(20) NOT NULL CHECK (calculation_mode IN ('WORKING_DAYS','CALENDAR_DAYS')),
    min_notice_days  INT         NOT NULL,
    active           BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE public_holidays (
    leave_parameter_id UUID  REFERENCES leave_parameters(id) ON DELETE CASCADE,
    holiday_date       DATE  NOT NULL,
    PRIMARY KEY (leave_parameter_id, holiday_date)
);

CREATE TABLE leave_balances (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id    UUID         NOT NULL,
    leave_type_id  UUID         NOT NULL REFERENCES leave_types(id),
    year           INT          NOT NULL,
    total_days     NUMERIC(5,1) NOT NULL,
    used_days      NUMERIC(5,1) NOT NULL DEFAULT 0,
    CONSTRAINT uk_employee_leave_year UNIQUE (employee_id, leave_type_id, year)
);

CREATE TABLE leave_requests (
    id                   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id          UUID        NOT NULL,
    leave_type_id        UUID        NOT NULL REFERENCES leave_types(id),
    start_date           DATE        NOT NULL,
    end_date             DATE        NOT NULL,
    working_days         INT         NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
                             CHECK (status IN ('DRAFT','SUBMITTED','VALIDATED','CONFIRMED','REJECTED')),
    rejection_reason     TEXT,
    justification_path   VARCHAR(500),
    validated_by_chef_id UUID,
    validated_at         TIMESTAMP,
    confirmed_by_rh_id   UUID,
    confirmed_at         TIMESTAMP,
    created_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_dates CHECK (end_date >= start_date)
);

CREATE INDEX idx_leave_requests_employee ON leave_requests(employee_id);
CREATE INDEX idx_leave_requests_status   ON leave_requests(status);
CREATE INDEX idx_leave_balances_employee ON leave_balances(employee_id);
