-- Fix existing resources to have is_file_upload = false (they are all URL resources)
UPDATE resources SET is_file_upload = false WHERE is_file_upload IS NULL;
