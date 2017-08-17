DELIMITER //
drop function if exists `getChildCompanyPersonList` //
CREATE FUNCTION `getChildCompanyPersonList`(rootId VARCHAR(50)) RETURNS longtext CHARSET utf8
BEGIN
	DECLARE
		pTemp longtext ;
DECLARE
	cTemp longtext ;

SET pTemp = '$';
SET cTemp = rootId;
WHILE cTemp IS NOT NULL DO

SET pTemp = concat(pTemp, ',', cTemp);
SELECT
	group_concat(uu.unitId) INTO cTemp
FROM
	(
		SELECT
			unit.unitId,
			unit.unitName,
			unit.parentId,
			unit.compType
		FROM
			SecurityUnitT unit
		WHERE
			unit.bsflag = '0'
		UNION
			SELECT
				u.userId AS 'unitId',
				u.userName AS 'unitName',
				u.belongOrg AS 'parentId',
				'3' AS 'compType'
			FROM
				SecurityUserT u
	) uu
WHERE
	FIND_IN_SET(uu.parentId, cTemp) > 0;
END
WHILE;
RETURN pTemp;

END//


DELIMITER ;