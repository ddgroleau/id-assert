DROP PROCEDURE IF EXISTS public.insert_role(character varying);

CREATE OR REPLACE PROCEDURE public.insert_role(IN role_name character varying)
LANGUAGE plpgsql
AS '
BEGIN
    IF NOT EXISTS (SELECT name from public.roles WHERE name = role_name) THEN
        INSERT INTO public.roles(created_by, created_on, name, updated_by, updated_on)
            VALUES (
            ''root'', --created_by
            CURRENT_DATE, --created_on
            role_name, --name
            ''root'', --updated_by
            CURRENT_DATE --updated_on
            );
    END IF;
END
';
-- ALTER PROCEDURE public.insert_role(character varying)
--     OWNER TO root;
