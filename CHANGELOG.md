# 1.2.0

- added proper support for Inventory Size Attributes
- added configurable item and exp cost for merging and splitting items
- added "merge_content_flags" boolean flags to merged items component, designed to be used by Spell Engine Extension
- fixed an issue where items of the same type could be merged
- fixed an issue where items could have multiple items of the same type merged into them
- fixed merged items tooltip displaying empty slots

# 1.1.0

- Item Merging Block rework
  - several settings are now saved in a block entity
  - added proper model
  - moved from the Operator to the Functional blocks creative inventory tab

- data components rework:
  - merged the two existing components into one
  - merged items are now shown in the tooltip of the container item (similar to the bundle tooltip)

- several small fixes, including a crash that could happen on game start

# 1.0.0

First release.

#