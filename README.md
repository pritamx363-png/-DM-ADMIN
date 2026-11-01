# Supabase setup notes

1. Create a free Supabase project (free tier availability/limits may change).
2. Run `schema.sql` in SQL Editor.
3. Create an Edge Function for admin CRUD and app-side license validation.
4. Keep `SUPABASE_SERVICE_ROLE_KEY` only in Edge Function secrets.
5. Add admin login/role checks before enabling create/edit/ban/unban/delete.
6. Validation must check status, expiry, device binding and device_limit on the server.
