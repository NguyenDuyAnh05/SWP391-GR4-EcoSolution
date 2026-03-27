INSERT INTO wards (ward_name) VALUES
                                  ('Hiep Binh'), ('Thu Duc'), ('Tam Binh'), ('Linh Xuan'),
                                  ('Tang Nhon Phu'), ('Long Binh'), ('Long Phuoc'), ('Long Truong'),
                                  ('Cat Lai'), ('Binh Trung'), ('Phuoc Long'), ('An Khanh');


INSERT INTO users (username, password, first_name, last_name, phone, role, created_at)
VALUES
    ('collector01', '123456', 'Nguyễn Văn', 'An', '0901000001', 'COLLECTOR', NOW()),
    ('collector02', '123456', 'Trần Thị', 'Bình', '0901000002', 'COLLECTOR', NOW()),
    ('collector03', '123456', 'Lê Văn', 'Cường', '0901000003', 'COLLECTOR', NOW()),
    ('collector04', '123456', 'Phạm Quốc', 'Đạt', '0901000004', 'COLLECTOR', NOW()),
    ('collector05', '123456', 'Hoàng Thanh', 'Hải', '0901000005', 'COLLECTOR', NOW()),
    ('collector06', '123456', 'Đỗ Minh', 'Khôi', '0901000006', 'COLLECTOR', NOW()),
    ('collector07', '123456', 'Vũ Tấn', 'Lộc', '0901000007', 'COLLECTOR', NOW()),
    ('collector08', '123456', 'Đặng Thành', 'Nam', '0901000008', 'COLLECTOR', NOW()),
    ('collector09', '123456', 'Bùi Xuân', 'Phúc', '0901000009', 'COLLECTOR', NOW()),
    ('collector10', '123456', 'Lý Hải', 'Quân', '0901000010', 'COLLECTOR', NOW());


INSERT INTO collection_points (name, address, latitude, longitude, ward_id) VALUES
                                                                                ('Trạm Thu gom rác tái chế Hiệp Bình', '123 Quốc Lộ 13, Phường Hiệp Bình', 10.827110, 106.723120, 1),
                                                                                ('Trạm Xanh trung tâm Thủ Đức', '45 Võ Văn Ngân, Phường Thủ Đức', 10.850230, 106.758990, 2),
                                                                                ('Điểm tập kết rác phân loại Hiệp Phú', '89 Lê Văn Việt, Phường Hiệp Phú', 10.847550, 106.793210, 3),
                                                                                ('Trạm tái chế Linh Xuân', '12 Quốc Lộ 1K, Phường Linh Xuân', 10.875660, 106.774330, 4),
                                                                                ('Trạm Eco Tăng Nhơn Phú', '56 Đình Phong Phú, Phường Tăng Nhơn Phú', 10.835880, 106.780440, 5),
                                                                                ('Trạm tập kết rác nguy hại Long Bình', '102 Nguyễn Xiển, Phường Long Bình', 10.852110, 106.835660, 6),
                                                                                ('Trạm Thu gom Long Phước', '200 Long Phước, Phường Long Phước', 10.825330, 106.850770, 7),
                                                                                ('Điểm xanh Nguyễn Duy Trinh', '345 Nguyễn Duy Trinh, Phường Long Trường', 10.812440, 106.820880, 8),
                                                                                ('Trạm tái chế Vòng Xoay Phú Hữu', '78 Bưng Ông Thoàn, Phường Phú Hữu', 10.801550, 106.802990, 9),
                                                                                ('Trạm thu gom khu Tân Phú', '12 Hoàng Hữu Nam, Phường Tân Phú', 10.865770, 106.812110, 10),
                                                                                ('Trạm Eco Đỗ Xuân Hợp', '456 Đỗ Xuân Hợp, Phường Phước Long', 10.818880, 106.768220, 11),
                                                                                ('Trạm Xanh Long Thạnh Mỹ', '89 Nguyễn Văn Tăng, Phường Long Thạnh Mỹ', 10.845990, 106.821330, 12);

INSERT INTO users (username, password, first_name, last_name, phone, role, created_at)
VALUES
    ('receiver01', '123456', 'Nguyễn Trực', 'Trạm 1', '0911000001', 'RECEIVER', NOW()),
    ('receiver02', '123456', 'Lê Trực', 'Trạm 2', '0911000002', 'RECEIVER', NOW()),
    ('receiver03', '123456', 'Trần Trực', 'Trạm 3', '0911000003', 'RECEIVER', NOW()),
    ('receiver04', '123456', 'Phạm Trực', 'Trạm 4', '0911000004', 'RECEIVER', NOW()),
    ('receiver05', '123456', 'Hoàng Trực', 'Trạm 5', '0911000005', 'RECEIVER', NOW()),
    ('receiver06', '123456', 'Vũ Trực', 'Trạm 6', '0911000006', 'RECEIVER', NOW()),
    ('receiver07', '123456', 'Đặng Trực', 'Trạm 7', '0911000007', 'RECEIVER', NOW()),
    ('receiver08', '123456', 'Bùi Trực', 'Trạm 8', '0911000008', 'RECEIVER', NOW()),
    ('receiver09', '123456', 'Đỗ Trực', 'Trạm 9', '0911000009', 'RECEIVER', NOW()),
    ('receiver10', '123456', 'Hồ Trực', 'Trạm 10', '0911000010', 'RECEIVER', NOW()),
    ('receiver11', '123456', 'Ngô Trực', 'Trạm 11', '0911000011', 'RECEIVER', NOW()),
    ('receiver12', '123456', 'Dương Trực', 'Trạm 12', '0911000012', 'RECEIVER', NOW());


UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver01') WHERE id = 1;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver02') WHERE id = 2;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver03') WHERE id = 3;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver04') WHERE id = 4;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver05') WHERE id = 5;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver06') WHERE id = 6;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver07') WHERE id = 7;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver08') WHERE id = 8;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver09') WHERE id = 9;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver10') WHERE id = 10;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver11') WHERE id = 11;
UPDATE collection_points SET receiver_id = (SELECT id FROM users WHERE username = 'receiver12') WHERE id = 12;