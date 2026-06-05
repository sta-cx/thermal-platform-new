-- Core replacement scope for 2026-06-05 acceptance.
-- Hide non-core charging desk menus. Keep payment-status import and write-card menus visible.

UPDATE sys_menu
SET visible = '1',
    status = '1'
WHERE menu_id IN (
    2307, -- expense item
    2308, -- fee standard
    2325, -- house expense
    2327, -- personal account
    2330, -- expense management
    2331  -- single charge
)
   OR parent_id IN (2307, 2308, 2325, 2327, 2330, 2331);

UPDATE sys_menu
SET visible = '0',
    status = '0'
WHERE menu_id IN (
    2332, -- import
    3663, -- write-card console
    3664  -- write-card log
);

