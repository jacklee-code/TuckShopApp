-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- 主機： localhost:3306
-- 產生時間： 2022 年 05 月 08 日 15:30
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
(27, 'admin1', 'Teacher One', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 3),
(28, 'jack', 'Jack Lee', 'e10adc3949ba59abbe56e057f20f883e', 4283.50, 1),
(29, 'parent1', 'Parent One', 'e10adc3949ba59abbe56e057f20f883e', 0.00, 2);

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
  `FoodId` varchar(30) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `Banned`
--

INSERT INTO `Banned` (`BanId`, `StudentId`, `FoodId`) VALUES
(19, 28, '4891338000024');

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
(40, 28, '2022-05-08 08:37:02'),
(41, 28, '2022-05-08 08:37:37'),
(42, 28, '2022-05-08 08:38:39');

-- --------------------------------------------------------

--
-- 資料表結構 `BuySlots`
--

CREATE TABLE `BuySlots` (
  `SlotId` int(11) NOT NULL,
  `RecordId` int(11) NOT NULL,
  `FoodId` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `Quantity` int(11) NOT NULL,
  `FoodName` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `Price` decimal(8,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `BuySlots`
--

INSERT INTO `BuySlots` (`SlotId`, `RecordId`, `FoodId`, `Quantity`, `FoodName`, `Price`) VALUES
(26, 40, '4895058314836', 1, 'Potato Chips', 11.00),
(27, 41, '351467107246', 2, 'Fanta', 10.50),
(28, 41, '4895058314836', 1, 'Potato Chips', 11.00),
(29, 41, '4897053380038', 2, 'Dreyer\'s Stick', 25.00),
(30, 41, '4895241100758', 1, 'Coca Cola', 11.00),
(31, 41, '6922266436956', 3, 'Doll Noodle', 8.50),
(32, 41, '4891338000024', 3, 'Fish Balls', 6.00),
(33, 41, '4892659057292', 5, 'Shao Mai', 6.00),
(34, 41, '4901005118737', 1, 'Pocky (Strawberry)', 15.00),
(35, 42, '4892659057292', 4, 'Shao Mai', 6.00);

-- --------------------------------------------------------

--
-- 資料表結構 `Foods`
--

CREATE TABLE `Foods` (
  `FoodId` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
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
('089782021014', 'Chocolate Finger', 11, 1, 80, 9.50),
('351467107246', 'Fanta', 17, 3, 89, 10.50),
('4891074020423', 'Fish Meat Sausage', 13, 2, 98, 6.00),
('4891338000024', 'Fish Balls', 18, 2, 132, 6.00),
('4892659057292', 'Shao Mai', 18, 2, 141, 6.00),
('4895058314836', 'Potato Chips', 12, 1, 68, 11.00),
('4895241100758', 'Coca Cola', 17, 3, 54, 11.00),
('4897038194858', 'Preserved Beef', 20, 1, 88, 7.50),
('4897053380038', 'Dreyer\'s Stick', 15, 2, 58, 25.00),
('4901005118737', 'Pocky (Strawberry)', 19, 1, 39, 15.00),
('6922266436956', 'Doll Noodle', 16, 2, 43, 8.50);

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
(25, 29, 28);

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
(11, 'Garden', 'Hong Kong-based bakery'),
(12, 'Pringles', 'Amercian brand'),
(13, 'EDO', 'Hong Kong-based company'),
(15, 'Nestlé', 'Swiss multinational company'),
(16, 'Nissin', 'Japanese company '),
(17, 'Swire', 'British Company'),
(18, 'Four Seas', 'Hong Kong Dim Sum Company'),
(19, 'Pocky', 'Japanese food company'),
(20, '759 Store', 'A Hong Kong store');

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
  ADD KEY `StudentId` (`StudentId`),
  ADD KEY `FoodId` (`FoodId`);

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
  MODIFY `UserId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `AccountType`
--
ALTER TABLE `AccountType`
  MODIFY `TypeId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Banned`
--
ALTER TABLE `Banned`
  MODIFY `BanId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `BuyRecords`
--
ALTER TABLE `BuyRecords`
  MODIFY `RecordId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `BuySlots`
--
ALTER TABLE `BuySlots`
  MODIFY `SlotId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `FoodType`
--
ALTER TABLE `FoodType`
  MODIFY `TypeId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Linkage`
--
ALTER TABLE `Linkage`
  MODIFY `LinkId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `Suppliers`
--
ALTER TABLE `Suppliers`
  MODIFY `SupplierId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

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
  ADD CONSTRAINT `Banned_ibfk_2` FOREIGN KEY (`StudentId`) REFERENCES `Accounts` (`UserId`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Banned_ibfk_3` FOREIGN KEY (`FoodId`) REFERENCES `Foods` (`FoodId`) ON DELETE CASCADE ON UPDATE CASCADE;

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
  ADD CONSTRAINT `BuySlots_ibfk_2` FOREIGN KEY (`FoodId`) REFERENCES `Foods` (`FoodId`) ON DELETE NO ACTION ON UPDATE CASCADE;

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
