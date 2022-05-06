-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- 主機： localhost:3306
-- 產生時間： 2022 年 05 月 06 日 14:27
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
(2, 'peter', 'Peter Park', 'e10adc3949ba59abbe56e057f20f883e', 79.67, 1),
(3, 'ben', 'Ben Ten', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(5, 'jack2', 'Jack 2', 'e10adc3949ba59abbe56e057f20f883e', 6.55, 1),
(9, 'kitty', 'Kitty', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(14, 'kitty2', 'Kitty 2', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(16, 'test', 'Test Account', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(18, 'test2', 'Testii', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 1),
(19, 'student', 'Student Type', '027be05dc526f69a60b26a9d32419ea8', 0.00, 1),
(21, 'type2', 'parent', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 2),
(22, 'jack', 'Jack Lee', 'e10adc3949ba59abbe56e057f20f883e', 465.70, 1),
(24, 'admin', 'Admin', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 3);

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
(12, 2, 2),
(15, 2, 5),
(16, 22, 4);

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
(28, 22, '2022-05-03 07:31:34'),
(29, 2, '2022-05-04 07:59:58'),
(30, 22, '2022-05-04 12:08:35'),
(31, 22, '2022-05-04 13:19:13'),
(32, 22, '2022-05-06 14:02:36'),
(33, 22, '2022-05-06 14:04:20'),
(34, 22, '2022-05-06 14:06:08'),
(35, 22, '2022-05-06 14:13:40'),
(36, 22, '2022-05-06 14:17:06');

-- --------------------------------------------------------

--
-- 資料表結構 `BuySlots`
--

CREATE TABLE `BuySlots` (
  `SlotId` int(11) NOT NULL,
  `RecordId` int(11) NOT NULL,
  `FoodId` int(11) NOT NULL,
  `Quantity` int(11) NOT NULL,
  `FoodName` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `Price` decimal(8,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `BuySlots`
--

INSERT INTO `BuySlots` (`SlotId`, `RecordId`, `FoodId`, `Quantity`, `FoodName`, `Price`) VALUES
(1, 27, 1, 1, 'Chips', 8.50),
(2, 27, 2, 1, 'Biscuits', 10.50),
(3, 28, 1, 5, 'Chips', 8.50),
(4, 28, 4, 1, 'Fish Balls', 4.00),
(5, 29, 1, 1, 'Chips', 8.50),
(6, 29, 4, 5, 'Fish Balls', 4.00),
(7, 30, 6, 2, 'Coca Cola', 11.00),
(9, 31, 6, 1, 'Coca Cola', 11.00),
(11, 32, 1, 1, 'Chips', 9.50),
(12, 32, 2, 1, 'Biscuits', 10.50),
(13, 32, 3, 1, 'Candy', 4.50),
(14, 32, 6, 1, 'Coca Cola', 11.00),
(15, 33, 1, 1, 'Chips', 6.50),
(16, 33, 2, 1, 'Biscuits', 4.50),
(17, 33, 3, 1, 'Candy', 4.50),
(18, 33, 6, 1, 'Coca Cola', 9.00),
(19, 34, 13, 5, '7up', 9.80),
(20, 35, 14, 1, 'Special food', 100.00),
(21, 36, 15, 1, 'special food', 100.00);

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
(1, 'Chips', 1, 1, 88, 6.50),
(2, 'Biscuits', 2, 1, 44, 4.50),
(3, 'Candy', 1, 1, 46, 4.50),
(4, 'Fish Balls', 2, 2, 41, 4.00),
(5, 'Shao Mai', 2, 2, 70, 6.00),
(6, 'Coca Cola', 2, 3, 94, 9.00);

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
(22, 21, 22),
(23, 21, 3);

-- --------------------------------------------------------

--
-- 資料表結構 `Suppliers`
--

CREATE TABLE `Suppliers` (
  `SupplierId` int(11) NOT NULL,
  `SupplierName` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `SupplierDescription` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `Suppliers`
--

INSERT INTO `Suppliers` (`SupplierId`, `SupplierName`, `SupplierDescription`) VALUES
(1, 'EdUHK', ''),
(2, 'IIT3008', '');

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
  ADD KEY `BuyItems_ibfk_1` (`RecordId`);

--
-- 資料表索引 `Foods`
--
ALTER TABLE `Foods`
  ADD PRIMARY KEY (`FoodId`),
  ADD KEY `Foods_ibfk_1` (`SupplierId`),
  ADD KEY `Foods_ibfk_2` (`TypeId`);

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
  MODIFY `UserId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `AccountType`
--
ALTER TABLE `AccountType`
  MODIFY `TypeId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Banned`
--
ALTER TABLE `Banned`
  MODIFY `BanId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `BuyRecords`
--
ALTER TABLE `BuyRecords`
  MODIFY `RecordId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `BuySlots`
--
ALTER TABLE `BuySlots`
  MODIFY `SlotId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Foods`
--
ALTER TABLE `Foods`
  MODIFY `FoodId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `FoodType`
--
ALTER TABLE `FoodType`
  MODIFY `TypeId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Linkage`
--
ALTER TABLE `Linkage`
  MODIFY `LinkId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Suppliers`
--
ALTER TABLE `Suppliers`
  MODIFY `SupplierId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

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
  ADD CONSTRAINT `BuySlots_ibfk_1` FOREIGN KEY (`RecordId`) REFERENCES `BuyRecords` (`RecordId`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 資料表的限制式 `Foods`
--
ALTER TABLE `Foods`
  ADD CONSTRAINT `Foods_ibfk_1` FOREIGN KEY (`SupplierId`) REFERENCES `Suppliers` (`SupplierId`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Foods_ibfk_2` FOREIGN KEY (`TypeId`) REFERENCES `FoodType` (`TypeId`) ON DELETE CASCADE ON UPDATE CASCADE;

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
