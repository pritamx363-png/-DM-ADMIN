-- DM ADMIN initial schema. Run in a new Supabase project.
-- Never expose service_role credentials in Android app code.
create extension if not exists pgcrypto;

create table if not exists public.licenses (
  id uuid primary key default gen_random_uuid(),
  license_key text not null unique check (license_key ~ '^AD[A-Z0-9-]{4,48}$'),
  alias text not null default '',
  device_limit integer not null default 1 check (device_limit > 0),
  expires_at timestamptz not null,
  status text not null default 'ACTIVE' check (status in ('ACTIVE','BANNED','EXPIRED')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.license_devices (
  id uuid primary key default gen_random_uuid(),
  license_id uuid not null references public.licenses(id) on delete cascade,
  device_hash text not null,
  first_seen_at timestamptz not null default now(),
  last_seen_at timestamptz not null default now(),
  unique (license_id, device_hash)
);

alter table public.licenses enable row level security;
alter table public.license_devices enable row level security;

-- Deliberately no anon/authenticated write policies.
-- Use authenticated Edge Functions with server-side role checks for all operations.
