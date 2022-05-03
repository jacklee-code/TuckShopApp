-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- 主機： localhost:3306
-- 產生時間： 2022 年 05 月 03 日 13:52
-- 伺服器版本： 10.5.12-MariaDB
-- PHP 版本： 7.3.32

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `id18260172_tuckshopdb`
--

-- --------------------------------------------------------

--
-- 資料表結構 `Accounts`
--

CREATE TABLE `Accounts` (
  `UserId` int(11) NOT NULL,
  `Username` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `Fullname` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `Password` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `Balance` decimal(11,2) NOT NULL DEFAULT 0.00,
  `TypeId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `Accounts`
--

INSERT INTO `Accounts` (`UserId`, `Username`, `Fullname`, `Password`, `Balance`, `TypeId`) VALUES
(2, 'peter', 'Peter Park', 'e10adc3949ba59abbe56e057f20f883e', 8.17, 1),
(3, 'ben', 'Ben Ten', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(5, 'jack2', 'Jack 2', 'e10adc3949ba59abbe56e057f20f883e', 6.55, 1),
(9, 'kitty', 'Kitty', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(14, 'kitty2', 'Kitty 2', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(16, 'test', 'Test Account', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(18, 'test2', 'Testii', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(19, 'student', 'Student Type', '027be05dc526f69a60b26a9d32419ea8', 0.00, 1),
(21, 'type2', 'parent', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 2),
(22, 'jack', 'Jack Lee', 'e10adc3949ba59abbe56e057f20f883e', 499.50, 1);

-- --------------------------------------------------------

--
-- 資料表結構 `AccountType`
--

CREATE TABLE `AccountType` (
  `TypeId` int(11) NOT NULL,
  `TypeName` varchar(20) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `AccountType`
--

INSERT INTO `AccountType` (`TypeId`, `TypeName`) VALUES
(1, 'Student'),
(2, 'Parent'),
(3, 'Teacher');

-- --------------------------------------------------------

--
-- 資料表結構 `Banned`
--

CREATE TABLE `Banned` (
  `BanId` int(11) NOT NULL,
  `StudentId` int(11) NOT NULL,
  `FoodId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `Banned`
--

INSERT INTO `Banned` (`BanId`, `StudentId`, `FoodId`) VALUES
(12, 2, 2);

-- --------------------------------------------------------

--
-- 資料表結構 `BuyRecords`
--

CREATE TABLE `BuyRecords` (
  `RecordId` int(11) NOT NULL,
  `StudentId` int(11) NOT NULL,
  `Time` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `BuyRecords`
--

INSERT INTO `BuyRecords` (`RecordId`, `StudentId`, `Time`) VALUES
(27, 22, '2022-05-03 07:04:51'),
(28, 22, '2022-05-03 07:31:34');

-- --------------------------------------------------------

--
-- 資料表結構 `BuySlots`
--

CREATE TABLE `BuySlots` (
  `SlotId` int(11) NOT NULL,
  `RecordId` int(11) NOT NULL,
  `FoodId` int(11) NOT NULL,
  `Quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `BuySlots`
--

INSERT INTO `BuySlots` (`SlotId`, `RecordId`, `FoodId`, `Quantity`) VALUES
(1, 27, 1, 1),
(2, 27, 2, 1),
(3, 28, 1, 5),
(4, 28, 4, 1);

-- --------------------------------------------------------

--
-- 資料表結構 `Foods`
--

CREATE TABLE `Foods` (
  `FoodId` int(11) NOT NULL,
  `FoodName` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `SupplierId` int(11) NOT NULL,
  `TypeId` int(11) NOT NULL,
  `Quantity` int(11) NOT NULL,
  `Price` decimal(8,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `Foods`
--

INSERT INTO `Foods` (`FoodId`, `FoodName`, `SupplierId`, `TypeId`, `Quantity`, `Price`) VALUES
(1, 'Chips', 1, 1, 42, 8.50),
(2, 'Biscuits', 2, 1, 46, 10.50),
(3, 'Candy', 1, 1, 48, 4.50),
(4, 'Fish Balls', 2, 2, 46, 4.00),
(5, 'Shao Mai', 2, 2, 1, 6.00),
(6, 'Coca Cola', 2, 3, 99, 11.00);

-- --------------------------------------------------------

--
-- 資料表結構 `FoodType`
--

CREATE TABLE `FoodType` (
  `TypeId` int(11) NOT NULL,
  `TypeName` varchar(20) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `FoodType`
--

INSERT INTO `FoodType` (`TypeId`, `TypeName`) VALUES
(1, 'Dry Food'),
(2, 'Wet Food'),
(3, 'Soft Drink');

-- --------------------------------------------------------

--
-- 資料表結構 `Linkage`
--

CREATE TABLE `Linkage` (
  `LinkId` int(11) NOT NULL,
  `ParentId` int(11) NOT NULL,
  `StudentId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `Linkage`
--

INSERT INTO `Linkage` (`LinkId`, `ParentId`, `StudentId`) VALUES
(15, 21, 22),
(17, 21, 2);

-- --------------------------------------------------------

--
-- 資料表結構 `Suppliers`
--

CREATE TABLE `Suppliers` (
  `SupplierId` int(11) NOT NULL,
  `SupplierName` varchar(20) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `Suppliers`
--

INSERT INTO `Suppliers` (`SupplierId`, `SupplierName`) VALUES
(1, 'EdUHK'),
(2, 'IIT3008');

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `Accounts`
--
ALTER TABLE `Accounts`
  ADD PRIMARY KEY (`UserId`),
  ADD UNIQUE KEY `Username` (`Username`),
  ADD KEY `AccountType` (`TypeId`);

--
-- 資料表索引 `AccountType`
--
ALTER TABLE `AccountType`
  ADD PRIMARY KEY (`TypeId`);

--
-- 資料表索引 `Banned`
--
ALTER TABLE `Banned`
  ADD PRIMARY KEY (`BanId`),
  ADD KEY `FoodId` (`FoodId`),
  ADD KEY `StudentId` (`StudentId`);

--
-- 資料表索引 `BuyRecords`
--
ALTER TABLE `BuyRecords`
  ADD PRIMARY KEY (`RecordId`),
  ADD KEY `StudentId` (`StudentId`);

--
-- 資料表索引 `BuySlots`
--
ALTER TABLE `BuySlots`
  ADD PRIMARY KEY (`SlotId`),
  ADD KEY `BuyItems_ibfk_1` (`RecordId`),
  ADD KEY `FoodId` (`FoodId`);

--
-- 資料表索引 `Foods`
--
ALTER TABLE `Foods`
  ADD PRIMARY KEY (`FoodId`),
  ADD KEY `SupplierId` (`SupplierId`),
  ADD KEY `TypeId` (`TypeId`);

--
-- 資料表索引 `FoodType`
--
ALTER TABLE `FoodType`
  ADD PRIMARY KEY (`TypeId`);

--
-- 資料表索引 `Linkage`
--
ALTER TABLE `Linkage`
  ADD PRIMARY KEY (`LinkId`),
  ADD KEY `ParentId` (`ParentId`),
  ADD KEY `StudentId` (`StudentId`);

--
-- 資料表索引 `Suppliers`
--
ALTER TABLE `Suppliers`
  ADD PRIMARY KEY (`SupplierId`);

--
-- 在傾印的資料表使用自動遞增(AUTO_INCREMENT)
--

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Accounts`
--
ALTER TABLE `Accounts`
  MODIFY `UserId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `AccountType`
--
ALTER TABLE `AccountType`
  MODIFY `TypeId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Banned`
--
ALTER TABLE `Banned`
  MODIFY `BanId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `BuyRecords`
--
ALTER TABLE `BuyRecords`
  MODIFY `RecordId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `BuySlots`
--
ALTER TABLE `BuySlots`
  MODIFY `SlotId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Foods`
--
ALTER TABLE `Foods`
  MODIFY `FoodId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `FoodType`
--
ALTER TABLE `FoodType`
  MODIFY `TypeId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Linkage`
--
ALTER TABLE `Linkage`
  MODIFY `LinkId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Suppliers`
--
ALTER TABLE `Suppliers`
  MODIFY `SupplierId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- 已傾印資料表的限制式
--

--
-- 資料表的限制式 `Accounts`
--
ALTER TABLE `Accounts`
  ADD CONSTRAINT `Accounts_ibfk_1` FOREIGN KEY (`TypeId`) REFERENCES `AccountType` (`TypeId`) ON UPDATE CASCADE;

--
-- 資料表的限制式 `Banned`
--
ALTER TABLE `Banned`
  ADD CONSTRAINT `Banned_ibfk_1` FOREIGN KEY (`FoodId`) REFERENCES `Foods` (`FoodId`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Banned_ibfk_2` FOREIGN KEY (`StudentId`) REFERENCES `Accounts` (`UserId`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 資料表的限制式 `BuyRecords`
--
ALTER TABLE `BuyRecords`
  ADD CONSTRAINT `BuyRecords_ibfk_1` FOREIGN KEY (`StudentId`) REFERENCES `Accounts` (`UserId`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 資料表的限制式 `BuySlots`
--
ALTER TABLE `BuySlots`
  ADD CONSTRAINT `BuySlots_ibfk_1` FOREIGN KEY (`RecordId`) REFERENCES `BuyRecords` (`RecordId`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `BuySlots_ibfk_2` FOREIGN KEY (`FoodId`) REFERENCES `Foods` (`FoodId`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 資料表的限制式 `Foods`
--
ALTER TABLE `Foods`
  ADD CONSTRAINT `Foods_ibfk_1` FOREIGN KEY (`SupplierId`) REFERENCES `Suppliers` (`SupplierId`) ON UPDATE CASCADE,
  ADD CONSTRAINT `Foods_ibfk_2` FOREIGN KEY (`TypeId`) REFERENCES `FoodType` (`TypeId`);

--
-- 資料表的限制式 `Linkage`
--
ALTER TABLE `Linkage`
  ADD CONSTRAINT `Linkage_ibfk_1` FOREIGN KEY (`ParentId`) REFERENCES `Accounts` (`UserId`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Linkage_ibfk_2` FOREIGN KEY (`StudentId`) REFERENCES `Accounts` (`UserId`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
