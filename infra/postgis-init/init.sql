-- =====================================================================
-- Habilita la extensión espacial PostGIS en la base de datos por defecto.
-- Este script se ejecuta únicamente la primera vez que se crea el
-- volumen de datos de la base de datos.
-- =====================================================================

CREATE EXTENSION IF NOT EXISTS postgis;
